package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvcore.PVCore;
import com.purityvanilla.pvcore.util.FormatCodeParser;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {
    private final PVChat plugin;

    public AsyncChatListener(PVChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Custom ChatRenderer defined in config

        // Replace permitted format codes with proper Component formatting
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        Component formattedMessage = PVCore.getAPI().parsePlayerFormatString(rawMessage, player, FormatCodeParser.Context.CHAT);
        event.message(formattedMessage);
    }
}
