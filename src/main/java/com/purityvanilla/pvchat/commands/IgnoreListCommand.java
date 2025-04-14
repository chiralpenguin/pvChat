package com.purityvanilla.pvchat.commands;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvcore.PVCore;
import com.purityvanilla.pvcore.util.CustomTagResolvers;
import com.purityvanilla.pvlib.commands.CommandGuard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.Set;
import java.util.UUID;

public class IgnoreListCommand implements CommandExecutor {
    private final PVChat plugin;

    public IgnoreListCommand(PVChat plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (CommandGuard.senderNotPlayer(sender, plugin.config().getMessage("player-only"))) return true;

        // Get ignored UUIDs as an array for iteration
        Player player = (Player) sender;
        Set<UUID> ignoredPlayers = PVCore.getAPI().getPlayerAPI().getIgnoredPlayers(player);
        UUID[] ignoredUuids = new UUID[ignoredPlayers.size()];
        ignoredUuids = ignoredPlayers.toArray(ignoredUuids);

        // Send different message if no players are ignored
        if (ignoredUuids.length == 0) {
            sender.sendMessage(plugin.config().getMessage("ignore-list-none"));
            return true;
        }

        // Build string containing list of ignored usernames
        StringBuilder ignoreList = new StringBuilder();
        for (int i = 0; i < ignoredUuids.length; i ++) {
            String username = PVCore.getAPI().getPlayerAPI().getPlayerUsername(ignoredUuids[i]);
            ignoreList.append(username);

            if (i < ignoredUuids.length - 1) {
                ignoreList.append(", ");
            }
        }

        sender.sendMessage(plugin.config().getMessage("ignore-list",
                TagResolver.resolver(Placeholder.component("ignorelist", Component.text(ignoreList.toString())))
        ));
        return true;
    }
}
