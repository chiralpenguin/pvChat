package com.purityvanilla.pvchat.commands;

import com.purityvanilla.pvchat.PVChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final PVChat plugin;

    public ReloadCommand(PVChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(plugin.config().getMessage("reload-plugin"));
        plugin.reload();
        sender.sendMessage(plugin.config().getMessage("reload-complete"));
        return true;
    }
}
