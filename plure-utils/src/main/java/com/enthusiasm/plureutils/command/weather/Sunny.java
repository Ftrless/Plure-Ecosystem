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

public class Sunny implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getServer().getOverworld();

        Message alreadySunny = TextUtils.translation("cmd.sunny.error.already_sunny", FormatUtils.Colors.ERROR);

        if (!world.isRaining() && !world.isThundering()) {
            throw CommandHelper.createException(alreadySunny);
        }

        WeatherManager.setSunny(world);
        PlayerUtils.sendFeedback(context, "cmd.sunny.feedback");
    }
}
