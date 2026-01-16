package com.purityvanilla.pvchat.filter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.text.Normalizer;
import java.util.Set;
import java.util.regex.Pattern;

public class TextFilter {
    private final MiniMessage mm;
    private final Set<Integer> blockedChars;
    private Pattern blockedCharPattern;
    private final String replacementChar;
    private final Set<String> blockedStrings;
    private final String replacementString;

    public TextFilter(Set<Integer> blockedChars, String replacementChar, Set<String> blockedStrings, String replacementString) {
        this.mm = buildStrictMiniMessage();

        this.blockedChars = blockedChars;
        this.blockedCharPattern = buildBlockedCharPattern();
        this.replacementChar = replacementChar;
        this.blockedStrings = blockedStrings;
        this.replacementString = replacementString;
    }

    private MiniMessage buildStrictMiniMessage() {
        // Only serialise permitted tags to avoid tag injection
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

    private Pattern buildBlockedCharPattern() {
        StringBuilder patternBuilder = new StringBuilder("[");
        for (int codePoint : blockedChars) {
            patternBuilder.append("\\x{").append(Integer.toHexString(codePoint)).append("}");
        }
        patternBuilder.append("]");

        return Pattern.compile(patternBuilder.toString());
    }

    public String replaceBannedCharacters(String text) {
        return blockedCharPattern.matcher(text).replaceAll(replacementChar);
    }

    public String resolveHomoglyphs(String text) {
        return "";
    }

    public String filterText(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC);
        String filtered = replaceBannedCharacters(normalized);

        return filtered;
    }

    public Component filterComponent(Component component) {
        String text = mm.serialize(component);
        String filtered = filterText(text);
        return mm.deserialize(filtered);
    }
}
