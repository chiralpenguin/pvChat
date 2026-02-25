package com.purityvanilla.pvchat;

import com.purityvanilla.pvchat.commands.IgnoreCommand;
import com.purityvanilla.pvchat.commands.IgnoreListCommand;
import com.purityvanilla.pvchat.commands.ReloadCommand;
import com.purityvanilla.pvchat.listeners.AsyncChatListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PVChat extends JavaPlugin {
    private Config config;

    @Override
    public void onEnable() {
        config = new Config(getServer().getPluginManager(), getLogger());

        registerCommands();
        registerListeners();
    }

    public void reload() {
        config = new Config(getServer().getPluginManager(), getLogger());
    }

    private void registerCommands() {
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
    }

    private void registerListeners() {
        // Bukkit event listeners
        getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
    }

    public Config config() {
        return config;
    }
}