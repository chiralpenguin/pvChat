package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvchat.chat.ChatFormat;
import com.purityvanilla.pvcore.PVCore;
import io.papermc.paper.event.player.AsyncChatEvent;
import com.purityvanilla.pvlib.util.FormatCodeParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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

        event.renderer((source, sourceDisplayName, message, viewer) ->
                ChatFormat.getRenderedMessage(
                        message,
                        source,
                        plugin.config().getRawMessage("chat-renderer")
                )
        );
    }
}
