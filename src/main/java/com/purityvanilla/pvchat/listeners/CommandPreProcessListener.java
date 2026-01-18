package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcessListener implements Listener {
    private final PVChat plugin;

    public CommandPreProcessListener(PVChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreProcessEvent(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String command = message.split(" ")[0].toLowerCase();

        String filtered = plugin.getTextFilter().filterText(message);
        event.setMessage(filtered);
    }
}
