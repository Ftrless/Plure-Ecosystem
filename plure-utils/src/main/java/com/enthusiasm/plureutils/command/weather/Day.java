package com.enthusiasm.plureutils.command.weather;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;

public class Day implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getServer().getOverworld();

        Message alreadyDaytime = TextUtils.translation("cmd.day.error.already_daytime", FormatUtils.Colors.ERROR);

        if (world.isDay()) {
            throw CommandHelper.createException(alreadyDaytime);
        }

        WeatherManager.setDay(world);
        PlayerUtils.sendFeedback(context, "cmd.day.feedback");
    }
}
