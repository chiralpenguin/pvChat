package com.purityvanilla.pvchat;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.purityvanilla.pvchat.commands.IgnoreCommand;
import com.purityvanilla.pvchat.commands.IgnoreListCommand;
import com.purityvanilla.pvchat.commands.MuteCommand;
import com.purityvanilla.pvchat.commands.ReloadCommand;
import com.purityvanilla.pvchat.filter.TextFilter;
import com.purityvanilla.pvchat.listeners.AsyncChatListener;
import com.purityvanilla.pvchat.listeners.CommandPreProcessListener;
import com.purityvanilla.pvchat.listeners.PrepareAnvilListener;
import com.purityvanilla.pvchat.listeners.packetevents.BossBarListener;
import com.purityvanilla.pvchat.listeners.packetevents.EditBookListener;
import com.purityvanilla.pvchat.listeners.packetevents.EntityMetaDataListener;
import com.purityvanilla.pvchat.listeners.packetevents.UpdateSignListener;
import com.purityvanilla.pvchat.mute.MuteDataService;
import com.purityvanilla.pvcore.PVCore;
import com.purityvanilla.pvlib.database.DataService;
import com.purityvanilla.pvlib.tasks.CacheCleanTask;
import com.purityvanilla.pvlib.tasks.SaveDataTask;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PVChat extends JavaPlugin {
    private Config config;
    private TextFilter textFilter;

    private HashMap<String, DataService> dataServices;

    @Override
    public void onEnable() {
        config = new Config(getLogger());
        textFilter =  new TextFilter(config);

        dataServices = new HashMap<>();
        dataServices.put("mute", new MuteDataService(this, PVCore.getAPI().getDatabase()));

        registerCommands();
        registerListeners();
        scheduleTasks();
    }

    @Override
    public void onDisable() {
        for (DataService service : dataServices.values()) {
           service.saveAll();
        }

        getLogger().info("Plugin disabled");
    }

   private void registerCommands() {
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));

        registerBrigadierCommands();
    }

    private void registerBrigadierCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();

            commands.register(new MuteCommand(this).buildCommand(), "Mute players", List.of());
        });
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

    private void scheduleTasks() {
        getServer().getGlobalRegionScheduler().cancelTasks(this);

        // Run saveData every 5 minutes after 1 minute
        SaveDataTask saveDataTask = new SaveDataTask(dataServices);
        getServer().getGlobalRegionScheduler().runAtFixedRate(
                this, task -> saveDataTask.run(),1200L, 6000L);

        // Run cacheClean every 10 minutes after 2 minute
        CacheCleanTask cacheCleanTask = new CacheCleanTask(dataServices);
        getServer().getGlobalRegionScheduler().runAtFixedRate(
                this, task -> cacheCleanTask.run(),2400L, 12000L);
    }

    public void reload() {
        config = new Config(getLogger());
        textFilter = new TextFilter(config);
    }

    public Config config() {
        return config;
    }

    public TextFilter getTextFilter() {
        return textFilter;
    }

    public MuteDataService getMuteData() {
        return (MuteDataService) dataServices.get("mute");
    }
}
