package com.purityvanilla.pvchat.listeners;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvcore.PVCore;
import com.purityvanilla.pvcore.util.FormatCodeParser;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        Player player = event.getPlayer();

        // Replace permitted format codes with proper Component formatting
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        Component formattedMessage = PVCore.getAPI().parsePlayerFormatString(rawMessage, player, FormatCodeParser.Context.CHAT);
        Component prefix = PVCore.getAPI().getPlayerAPI().getPlayerPrefix(player);
        Component suffix = PVCore.getAPI().getPlayerAPI().getPlayerSuffix(player);

        // Custom ChatRenderer defined as message
        event.renderer((source, sourceDisplayName, message, viewer) ->
                plugin.config().getMessage("chat-renderer",
                TagResolver.resolver(
                        Placeholder.component("prefix", prefix),
                        Placeholder.component("displayname", player.displayName()),
                        Placeholder.component("suffix", suffix),
                        Placeholder.component("message", formattedMessage)
                )
        ));
    }
}
