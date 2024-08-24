package com.enthusiasm.plureutils.command.restart;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.TimeUtils;
import com.enthusiasm.plureutils.service.RestartService;

public class RestartAt implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long remainingTime = RestartService.getRemainingTime();

        PlayerUtils.sendFeedback(context, "cmd.restart.at.feedback", TimeUtils.getFormattedRemainingTime(remainingTime));
    }
}
