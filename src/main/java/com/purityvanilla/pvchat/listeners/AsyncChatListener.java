package com.purityvanilla.pvchat.listeners;

import com.destroystokyo.paper.ClientOption;
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
                    return PVCore.getAPI().getPlayerAPI().isPlayerIgnored(recipient, sender) ||
                            recipient.getClientOption(ClientOption.CHAT_VISIBILITY) != ClientOption.ChatVisibility.FULL;
                }
                return false;
            });
        }

        ChatRenderer renderer = (source, sourceDisplayName, message, viewer) ->
                ChatFormat.formatMessage(
                        message,
                        source,
                        plugin.config().getRawMessage("chat-renderer")
                );

        if (plugin.config().isContentFilterEnabled()) handleFiltering(event, sender, renderer);

        // Apply renderer to the event (for the sender's message)
        event.renderer(renderer);
    }

    private void handleFiltering(AsyncChatEvent event, Player sender, ChatRenderer renderer) {
        Component original = event.message();
        Component filtered = plugin.getTextFilter().filterComponent(original);

        if (!original.equals(filtered)) {
            Set<Audience> others = new HashSet<>(event.viewers());
            others.remove(sender);

            event.viewers().clear();
            event.viewers().add(sender);

            // Render the filtered message using the same format
            Component display = renderer.render(
                    sender,
                    sender.displayName(),
                    filtered,
                    Audience.empty()
            );

            for (Audience viewer : others) {
                viewer.sendMessage(display);
            }
        }
    }
}
