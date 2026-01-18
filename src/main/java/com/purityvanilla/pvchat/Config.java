package com.purityvanilla.pvchat;

import com.purityvanilla.pvlib.config.ConfigFile;
import com.purityvanilla.pvlib.config.Messages;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Config extends ConfigFile {
    private final Set<Integer> blockedChars;
    private final Set<String> blockedStrings;
    private final boolean enableContentFilter;
    private final String replacementString;
    private final List<String> muteBlockedCommands;
    private final boolean verbose;

    public Config(Logger logger) {
        super("plugins/pvChat/config.yml");
        messages = new Messages(this, "plugins/pvChat/messages.json");

        Set<Integer> loadedChars = Set.of();
        try {
            loadedChars = readBlockedCharacters("plugins/pvChat/blocked_chars.txt");
        } catch (IOException e) {
            logger.severe("Could not read blocked_chars.txt! Ensure the file exists and is valid.");
        }
        blockedChars = loadedChars;

        Set<String> loadedStrings = Set.of();
        try {
            loadedStrings = readBlockedStrings("plugins/pvChat/blocked_words.txt");
        } catch (IOException e) {
            logger.severe("Could not read blocked_chars.txt! Ensure the file exists and is valid.");
        }
        blockedStrings = loadedStrings;

        enableContentFilter = configRoot.node("enable-content-filter").getBoolean();
        replacementString = configRoot.node("replacement-string").getString();

        List<String> muteCommands = new ArrayList<>();
        try {
            muteCommands = configRoot.node("mute-blocked-commands").getList(String.class);
        } catch (SerializationException e) {
            logger.severe("Could not read 'mute-blocked-commands' value. Verify config.yml");
        }
        muteBlockedCommands = muteCommands;

        verbose = configRoot.node("verbose").getBoolean();
    }

    private Set<Integer> readBlockedCharacters(String filepath) throws IOException {
        Set<Integer> blocked = new HashSet<>();

        try (BufferedReader buffer = new BufferedReader(new FileReader(filepath))) {
            String line = buffer.readLine();

            while (line != null) {
                blocked.add(Integer.decode(line));
                line = buffer.readLine();
            }
        }

        return blocked;
    }

    private Set<String> readBlockedStrings(String filepath) throws IOException {
        Set<String> blocked = new HashSet<>();

        try (BufferedReader buffer = new BufferedReader(new FileReader(filepath))) {
            String line = buffer.readLine();

            while (line != null) {
                blocked.add(line);
                line = buffer.readLine();
            }
        }

        return blocked;
    }

    public Set<Integer> getBlockedChars() {
        return blockedChars;
    }

    public Set<String> getBlockedStrings() {
        return blockedStrings;
    }

    public boolean isContentFilterEnabled() {
        return enableContentFilter;
    }

    public String getReplacementString() {
        return replacementString;
    }

    public List<String> getMuteBlockedCommands() {
        return muteBlockedCommands;
    }

    public boolean verbose() {
        return this.verbose;
    }
}
