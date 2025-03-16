package com.purityvanilla.pvchat;

import com.purityvanilla.pvlib.config.ConfigFile;
import com.purityvanilla.pvlib.config.Messages;

public class Config extends ConfigFile {
    private final boolean verbose;

    public Config() {
        super("plugins/pvChat/config.yml");
        messages = new Messages(this, "plugins/pvChat/messages.json");

        verbose = configRoot.node("verbose").getBoolean();
    }

    public boolean verbose() {
        return this.verbose;
    }
}
