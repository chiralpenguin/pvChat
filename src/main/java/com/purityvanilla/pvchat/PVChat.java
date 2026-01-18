package com.purityvanilla.pvchat;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.purityvanilla.pvchat.commands.IgnoreCommand;
import com.purityvanilla.pvchat.commands.IgnoreListCommand;
import com.purityvanilla.pvchat.commands.ReloadCommand;
import com.purityvanilla.pvchat.filter.TextFilter;
import com.purityvanilla.pvchat.listeners.AsyncChatListener;
import com.purityvanilla.pvchat.listeners.CommandPreProcessListener;
import com.purityvanilla.pvchat.listeners.PrepareAnvilListener;
import com.purityvanilla.pvchat.listeners.packetevents.BossBarListener;
import com.purityvanilla.pvchat.listeners.packetevents.EditBookListener;
import com.purityvanilla.pvchat.listeners.packetevents.EntityMetaDataListener;
import com.purityvanilla.pvchat.listeners.packetevents.UpdateSignListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

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
        // Bukkit event listeners
        getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandPreProcessListener(this), this);
        getServer().getPluginManager().registerEvents(new PrepareAnvilListener(this), this);

        // PacketEvents packet listeners
        List<PacketListener> packetListeners = List.of(
                new BossBarListener(this),
                new EditBookListener(this),
                new EntityMetaDataListener(this),
                new UpdateSignListener(this)
        );

        for (PacketListener packetListener : packetListeners) {
            PacketEvents.getAPI().getEventManager().registerListener(packetListener, PacketListenerPriority.NORMAL);
        }
    }

    public Config config() {
        return config;
    }

    public TextFilter getTextFilter() {
        return textFilter;
    }
}
