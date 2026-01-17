package com.purityvanilla.pvchat.filter;

/**
 * Represents a single character in a {@link TrackedString}, maintaining a mapping
 * back to its origin in the original input.
 *
 * <p>Example: collapsing "niiiiice" produces a {@code CharSpan('i', 1, 5, false)}
 * representing the four i's at original indices 1-5.
 *
 * @param transformed the character after pipeline transformations
 * @param origStart   inclusive start index in the original string
 * @param origEnd     inclusive end index in the original string
 * @param ignored     whether this character is excluded from matching (e.g. whitespace)
 */
public record CharSpan(
    char transformed,
    int origStart,
    int origEnd,
    boolean ignored
) {
    public CharSpan(char transformed, int origIndex) {
        this(transformed, origIndex, origIndex, false);
    }

    public CharSpan withChar(char c) {
        return new CharSpan(c, origStart, origEnd, ignored);
    }

    public CharSpan withIgnored(boolean ignored) {
        return new CharSpan(transformed, origStart, origEnd, ignored);
    }
}
