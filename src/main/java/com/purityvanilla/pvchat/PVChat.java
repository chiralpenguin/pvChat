package com.purityvanilla.pvchat;

import org.bukkit.plugin.java.JavaPlugin;

public class PVChat extends JavaPlugin {
    private Config config;

    @Override
    public void onEnable() {
        config = new Config();

        registerListeners();
    }

    private void registerListeners() {

    }
}
