package com.purityvanilla.pvchat;

import com.purityvanilla.pvchat.commands.ReloadCommand;
import com.purityvanilla.pvchat.listeners.AsyncChatListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PVChat extends JavaPlugin {
    private Config config;

    @Override
    public void onEnable() {
        config = new Config();

        registerCommands();
        registerListeners();
    }

    public Config config() {
        return config;
    }

    public void reload() {
        config = new Config();
    }

    private void registerCommands() {
        getCommand("reload").setExecutor(new ReloadCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
    }
}
