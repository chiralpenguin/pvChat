package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
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

        // Replace permitted format codes with proper Component formatting
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        Component formattedMessage = FormatCodeParser.parseString(rawMessage, sender, FormatCodeParser.Context.CHAT);
        Component prefix = PVCore.getAPI().getPlayerAPI().getPlayerPrefix(sender);
        Component displayName = sender.displayName();
        Component suffix = PVCore.getAPI().getPlayerAPI().getPlayerSuffix(sender);

        // Apply any style from prefix to displayname if it has no existing style
        Style prefixStyle = prefix.style();
        Style displayNameStyle = displayName.style();
        Component finalDisplayName;

        if (displayNameStyle.isEmpty()) {
            finalDisplayName = displayName.style(prefixStyle);
        } else {
            Style combinedStyle = prefixStyle.merge(displayNameStyle);
            finalDisplayName = displayName.style(combinedStyle);
        }

        // Custom ChatRenderer defined as message
        event.renderer((source, sourceDisplayName, message, viewer) ->
                plugin.config().getMessage("chat-renderer",
                TagResolver.resolver(
                        Placeholder.component("prefix", prefix),
                        Placeholder.component("displayname", finalDisplayName),
                        Placeholder.component("suffix", suffix),
                        Placeholder.component("message", formattedMessage)
                )
        ));
    }
}
