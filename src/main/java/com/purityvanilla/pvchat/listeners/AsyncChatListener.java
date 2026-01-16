package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvchat.util.ChatFormat;
import com.purityvanilla.pvcore.PVCore;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {
    private final PVChat plugin;

    public AsyncChatListener(PVChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        // Prevent players who have the sender ignored from receiving the message
        if (!sender.hasPermission("pvchat.ignore.bypass")) {
            event.viewers().removeIf(audience -> {
                if (audience instanceof Player recipient) {
                    return PVCore.getAPI().getPlayerAPI().isPlayerIgnored(recipient, sender);
                }
                return false;
            });
        }

        // TODO filter raw message string here, Component->Plaintext->Component

        event.renderer((source, sourceDisplayName, message, viewer) ->
                ChatFormat.formatMessage(
                        message,
                        source,
                        plugin.config().getRawMessage("chat-renderer")
                )
        );
    }
}
