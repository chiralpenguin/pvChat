package com.purityvanilla.pvchat;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.purityvanilla.pvchat.commands.IgnoreCommand;
import com.purityvanilla.pvchat.commands.IgnoreListCommand;
import com.purityvanilla.pvchat.commands.ReloadCommand;
import com.purityvanilla.pvchat.filter.TextFilter;
import com.purityvanilla.pvchat.listeners.AsyncChatListener;
import com.purityvanilla.pvchat.listeners.packetevents.BossBarListener;
import com.purityvanilla.pvchat.listeners.packetevents.EntityMetaDataListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PVChat extends JavaPlugin {
    private Config config;
    private TextFilter textFilter;

    @Override
    public void onEnable() {
        config = new Config(getLogger());
        textFilter =  new TextFilter(config);

        registerCommands();
        registerListeners();
    }


    public void reload() {
        config = new Config(getLogger());
        textFilter = new TextFilter(config);
    }

    private void registerCommands() {
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
        PacketEvents.getAPI().getEventManager().registerListener(new BossBarListener(this),
                PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().getEventManager().registerListener(new EntityMetaDataListener(this),
                PacketListenerPriority.NORMAL);
    }

    public Config config() {
        return config;
    }

    public TextFilter getTextFilter() {
        return textFilter;
    }
}
