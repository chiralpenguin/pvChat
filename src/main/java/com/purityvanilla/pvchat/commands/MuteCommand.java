package com.purityvanilla.pvchat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.purityvanilla.pvchat.PVChat;
import com.purityvanilla.pvlib.PVLib;
import com.purityvanilla.pvlib.util.CommandArgumentHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class MuteCommand {
    private final PVChat plugin;

    public MuteCommand(PVChat plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> buildCommand() {
        return Commands.literal("mute")
                .requires(source -> source.getSender().hasPermission("pvchat.mute"))
                .then(Commands.argument("player", StringArgumentType.string())
                    .executes(this::executePermMutePlayer)
                    .then(Commands.argument("duration", StringArgumentType.string())
                        .executes(this::executeMutePlayer)
                    )
                )
                .build();
    }

    private int executeMutePlayer(CommandContext<CommandSourceStack> ctx) {
        String timeString = ctx.getArgument("duration", String.class).toLowerCase();
        long duration = CommandArgumentHelper.parseTimeSeconds(timeString);

        return handleMute(ctx, duration);
    }

    private int executePermMutePlayer(CommandContext<CommandSourceStack> ctx) {
        return handleMute(ctx, -1);
    }

    private int handleMute(CommandContext<CommandSourceStack> ctx, long duration) {
        CommandSender sender = ctx.getSource().getSender();

        OfflinePlayer target = plugin.getServer().getOfflinePlayer(ctx.getArgument("player", String.class));
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(plugin.config().getMessage("player-not-found"));
            return Command.SINGLE_SUCCESS;
        }

        UUID targetID = target.getUniqueId();
        Timestamp expiration = duration > 0 ? Timestamp.from(Instant.now().plusSeconds(duration)) : null;

        plugin.getMuteData().mutePlayer(targetID, expiration);
    }
}
