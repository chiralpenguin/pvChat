package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvchat.util.ChatFormat;
import com.purityvanilla.pvcore.PVCore;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

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

        Component original = event.message();
        Component censored = plugin.getTextFilter().filterComponent(original);

        // Create the renderer
        ChatRenderer renderer = (source, sourceDisplayName, message, viewer) ->
                ChatFormat.formatMessage(
                        message,
                        source,
                        plugin.config().getRawMessage("chat-renderer")
                );

        if (!original.equals(censored)) {
            Set<Audience> others = new HashSet<>(event.viewers());
            others.remove(sender);

            event.viewers().clear();
            event.viewers().add(sender);

            // Render the censored message using the same format
            Component display = renderer.render(
                    sender,
                    sender.displayName(),
                    censored,
                    Audience.empty()
            );

            for (Audience viewer : others) {
                viewer.sendMessage(display);
            }
        }

        // Apply renderer to the event (for the sender's message)
        event.renderer(renderer);
    }
}
