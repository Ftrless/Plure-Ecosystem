package com.enthusiasm.plureutils.command.restart;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.TimeUtils;
import com.enthusiasm.plureutils.service.RestartService;

public class RestartForce implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String time = StringArgumentType.getString(context, "time");

        exec(context, TimeUtils.parseDuration(time));

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, long time) throws CommandSyntaxException {
        RestartService.forceRestart(time);

        PlayerUtils.sendFeedback(context, "cmd.restart.force.feedback");
    }
}
