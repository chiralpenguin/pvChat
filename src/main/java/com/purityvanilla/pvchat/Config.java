package com.purityvanilla.pvchat;

import com.purityvanilla.pvlib.config.ConfigFile;
import com.purityvanilla.pvlib.config.Messages;
import org.bukkit.plugin.PluginManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Config extends ConfigFile {
    private final boolean enableContentFilter;
    private final boolean verbose;

    public Config(PluginManager pluginManager, Logger logger) {
        super("plugins/pvChat/config.yml");
        messages = new Messages(this, "plugins/pvChat/messages.json");

        boolean willEnableContentFilter = configRoot.node("enable-content-filter").getBoolean();
        if (willEnableContentFilter && !pluginManager.isPluginEnabled("pvFilter")) {
            logger.severe("Content filter cannot be enabled because pvFilter is not installed!");
            willEnableContentFilter = false;
        }

        enableContentFilter = willEnableContentFilter;
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

    public boolean contentFilterEnabled() {
        return enableContentFilter;
    }

    public boolean verbose() {
        return this.verbose;
    }
}
