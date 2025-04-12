package com.purityvanilla.pvchat.commands;

import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvcore.PVCore;
import com.purityvanilla.pvcore.api.player.PlayerAPI;
import com.purityvanilla.pvcore.util.CustomTagResolvers;
import com.purityvanilla.pvlib.commands.CommandGuard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IgnoreCommand implements CommandExecutor {
    private final PVChat plugin;

    public IgnoreCommand(PVChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (CommandGuard.senderNotPlayer(sender, plugin.config().getMessage("player-only"))) return true;
        if (CommandGuard.argsSizeInvalid(1, args, sender, plugin.config().getMessage("ignore-usage"))) return true;

        Player player = (Player) sender;
        Player target = plugin.getServer().getPlayer(args[0].toLowerCase());
        if (target == null) {
            sender.sendMessage(plugin.config().getMessage("player-not-found"));
            return true;
        }

        // Prevent players from ignoring themselves
        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(plugin.config().getMessage("ignore-self"));
            return true;
        }

        PlayerAPI playerAPI = PVCore.getAPI().getPlayerAPI();
        if (playerAPI.isPlayerIgnored(player, target)) {
            playerAPI.unignorePlayer(player, target);
            sender.sendMessage(plugin.config().getMessage("unignore-player", CustomTagResolvers.playerResolver(target.getName())));
            return true;
        }

        playerAPI.ignorePlayer(player, target);
        sender.sendMessage(plugin.config().getMessage("ignore-player", CustomTagResolvers.playerResolver(target.getName())));
        return true;
    }
}
