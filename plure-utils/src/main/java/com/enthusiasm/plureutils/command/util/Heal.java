package com.enthusiasm.plureutils.command.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;

public class Heal implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        senderPlayer.setHealth(senderPlayer.getMaxHealth());

        PlayerUtils.sendFeedback(context, "cmd.heal.feedback");
    }
}
