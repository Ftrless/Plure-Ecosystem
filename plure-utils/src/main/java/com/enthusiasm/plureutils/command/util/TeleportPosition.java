package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.WorldUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TeleportPosition implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Vec3d position = Vec3ArgumentType.getVec3(context, "position");
        String world = StringArgumentType.getString(context, "world");

        exec(
                context,
                position,
                WorldUtils.getServerWorld(world, context.getSource().getServer(), true)
        );

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, Vec3d position, ServerWorld world) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

        PlayerUtils.teleportPlayer(
                player,
                position.getX(),
                position.getY(),
                position.getZ(),
                player.getYaw(),
                player.getPitch(),
                world
        );

        PlayerUtils.sendFeedback(context, "cmd.tppos.feedback",
                String.format("%.0f", position.getX()),
                String.format("%.0f", position.getY()),
                String.format("%.0f", position.getZ())
        );
    }
}
