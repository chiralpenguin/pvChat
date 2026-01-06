package com.purityvanilla.pvchat.chat;

import com.purityvanilla.pvcore.PVCore;
import com.purityvanilla.pvlib.util.FormatCodeParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class ChatFormat {

    public static Component getRenderedMessage(Component message, Player source, String rendererTemplate) {
        // Replace permitted format codes with proper Component formatting
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(message);
        Component formattedMessage = FormatCodeParser.parseString(rawMessage, source, FormatCodeParser.Context.CHAT);
        Component prefix = PVCore.getAPI().getPlayerAPI().getPlayerPrefix(source);
        Component displayName = source.displayName();
        Component suffix = PVCore.getAPI().getPlayerAPI().getPlayerSuffix(source);

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

        return MiniMessage.miniMessage().deserialize(
                rendererTemplate,
                TagResolver.resolver(
                        Placeholder.component("prefix", prefix),
                        Placeholder.component("displayname", finalDisplayName),
                        Placeholder.component("suffix", suffix),
                        Placeholder.component("message", formattedMessage)
                ));
    }
}
