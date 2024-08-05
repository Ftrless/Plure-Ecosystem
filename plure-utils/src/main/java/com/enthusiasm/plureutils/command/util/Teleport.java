package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class Teleport implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, Vec3d position, ServerWorld world) throws CommandSyntaxException {
        PlayerUtils.sendFeedback(context, "cmd.teleport.feedback");
    }
}
