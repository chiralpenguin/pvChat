package com.purityvanilla.pvchat.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A string representation that tracks the provenance of each character through
 * a series of transformations, enabling reconstruction of the original input
 * with targeted modifications.
 *
 * <p>Example flow:
 * <pre>{@code
 * Original:    "N.i" (indices 0, 1, 2)
 * CharSpans:   [N:0, .:1, i:2] → after ignore stage → [N:0, .:1(ignored), i:2]
 * Matchable:   "ni" (indices 0, 1)
 * Mapping:     matchable[0] → CharSpan[0], matchable[1] → CharSpan[2]
 * }</pre>
 */
public class TrackedString {
    private final List<CharSpan> chars;
    private String matchable;
    private int[] matchableSpanMap;

    public TrackedString(List<CharSpan> chars) {
        this.chars = chars;
    }

    public static TrackedString from(String original) {
        List<CharSpan> chars = new ArrayList<>(original.length());
        for (int i = 0; i < original.length(); i++) {
            chars.add(new CharSpan(original.charAt(i), i));
        }
        return new TrackedString(chars);
    }

    private void buildMatchable() {
        StringBuilder sb = new StringBuilder(chars.size());
        int[] mapping = new int[chars.size()]; // Allocate for worst case with nothing ignored/collapsed
        int count = 0;

        for (int i = 0; i < chars.size(); i++) {
            if (chars.get(i).ignored()) continue;

            sb.append(chars.get(i).transformed());
            mapping[count++] = i;
        }

        this.matchable = sb.toString();
        this.matchableSpanMap = (count == mapping.length) ? mapping : Arrays.copyOf(mapping, count);
    }

    public List<CharSpan> chars() {
        return chars;
    }

    public int size() {
        return chars.size();
    }

    public String getMatchable() {
        if (matchable == null) {
            buildMatchable();
        }
        return  matchable;
    }

    public int getSpanIndex(int matchableIndex) {
        if (matchableSpanMap == null) {
            buildMatchable();
        }
        return matchableSpanMap[matchableIndex];
    }

    public int getOrigStart(int spanIndex) {
        return chars.get(spanIndex).origStart();
    }

    public int getOrigEnd(int spanIndex) {
        return chars.get(spanIndex).origEnd();
    }
}