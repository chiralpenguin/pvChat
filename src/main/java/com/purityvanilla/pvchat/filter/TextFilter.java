package com.purityvanilla.pvchat.filter;

import com.ibm.icu.text.SpoofChecker;
import com.purityvanilla.pvchat.Config;
import com.purityvanilla.pvchat.filter.stages.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextFilter {
    private final MiniMessage mm;
    private final List<FilterStage> pipeline;

    private Pattern blockedCharPattern;
    private final Set<String> blockedStrings;
    private final Pattern blockedStringsPattern;
    private final String replacementString;

    public TextFilter(Set<Integer> blockedChars, Set<String> blockedStrings, String replacementString) {
        this.mm = buildStrictMiniMessage();
        this.pipeline = buildFilterPipeline();

        this.blockedCharPattern = buildBlockedCharPattern(blockedChars);
        this.blockedStrings = blockedStrings;
        this.blockedStringsPattern = buildBlockedStringsPattern();
        this.replacementString = replacementString;
    }

    public TextFilter(Config config) {
        this(
                config.getBlockedChars(),
                config.getBlockedStrings(),
                config.getReplacementString()
        );
    }

    private MiniMessage buildStrictMiniMessage() {
        return MiniMessage.builder()
                .strict(true)
                .tags(TagResolver.builder()
                        .resolvers(
                                StandardTags.color(),
                                StandardTags.decorations(),
                                StandardTags.reset()
                        ).build()
                ).build();
    }

    private List<FilterStage> buildFilterPipeline() {
        return List.of(
                new LowercaseStage(),
                new LeetspeakStage(),
                new HomoglyphStage(),
                new IgnoreNonAlphaStage(),
                new CollapseRepeatStage()
        );
    }

    private Pattern buildBlockedCharPattern(Set<Integer> blockedChars) {
        StringBuilder patternBuilder = new StringBuilder("[");
        for (int codePoint : blockedChars) {
            patternBuilder.append("\\x{").append(Integer.toHexString(codePoint)).append("}");
        }
        patternBuilder.append("]");

        return Pattern.compile(patternBuilder.toString());
    }

    private Pattern buildBlockedStringsPattern() {
        if (blockedStrings.isEmpty()) {
            return Pattern.compile("(?!)");
        }

        StringBuilder patternBuilder = new StringBuilder("(");
        boolean first = true;
        for (String blocked : blockedStrings) {
            if (!first) {
                patternBuilder.append("|");
            }
            first = false;

            patternBuilder.append(Pattern.quote(blocked.toLowerCase()));
        }
        patternBuilder.append(")");

        return Pattern.compile(patternBuilder.toString());
    }

    private String removeBannedCharacters(String text) {
        return blockedCharPattern.matcher(text).replaceAll("");
    }

    private List<FilterMatch> findMatches(TrackedString ts) {
        List<FilterMatch> matches = new ArrayList<>();
        Matcher matcher = blockedStringsPattern.matcher(ts.getMatchable());

        while (matcher.find()) {
            int start = ts.getSpanIndex(matcher.start());
            int end = ts.getSpanIndex(matcher.end() - 1);
            matches.add(new FilterMatch(start, end));
        }

        return matches;
    }

    private String reconstructText(String original, List<FilterMatch> matches, TrackedString ts) {
        StringBuilder result = new StringBuilder();
        int cursor = 0;

        for (FilterMatch match : matches) {
            int origStart = ts.getOrigStart(match.start());
            int origEnd = ts.getOrigEnd(match.end());

            result.append(original, cursor, origStart);
            result.append(replacementString);
            cursor = origEnd + 1;
        }

        result.append(original, cursor, original.length());
        return result.toString();
    }

    public String filterText(String text) {
        // Apply symbol removal and NFKC normalisation directly as do not need to reconstruct these characters
        String symbolRemoved = removeBannedCharacters(text);
        String normalized = Normalizer.normalize(symbolRemoved, Normalizer.Form.NFKC);

        TrackedString ts = TrackedString.from(normalized);

        for (FilterStage filter : pipeline) {
            ts = filter.apply(ts);
        }

        List<FilterMatch> matches = findMatches(ts);
        if (matches.isEmpty()) {
            return symbolRemoved;
        }

        return reconstructText(normalized, matches, ts);
    }

    public Component filterComponent(Component component) {
        String text = mm.serialize(component);
        String filtered = filterText(text);
        return mm.deserialize(filtered);
    }
}