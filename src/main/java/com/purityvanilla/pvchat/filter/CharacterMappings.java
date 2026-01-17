package com.purityvanilla.pvchat.filter;

import java.util.Map;

public class CharacterMappings {

    private static final Map<Character, Character> LEET_SPEAK = Map.ofEntries(
            Map.entry('4', 'a'),
            Map.entry('@', 'a'),
            Map.entry('8', 'b'),
            Map.entry('(', 'c'),
            Map.entry('{', 'c'),
            Map.entry('3', 'e'),
            Map.entry('6', 'g'),
            Map.entry('9', 'g'),
            Map.entry('1', 'i'),
            // Map.entry('!', 'i'), Breaks tag negation, must be blocked seperately
            Map.entry('|', 'i'),
            Map.entry('0', 'o'),
            Map.entry('5', 's'),
            Map.entry('$', 's'),
            Map.entry('7', 't'),
            Map.entry('+', 't'),
            Map.entry('¥', 'y'),
            Map.entry('2', 'z')
    );

    public static Character resolveLeet(Character key) {
        return LEET_SPEAK.getOrDefault(key, key);
    }
}
