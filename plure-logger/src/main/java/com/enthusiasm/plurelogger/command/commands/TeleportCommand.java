package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import com.enthusiasm.plurelogger.PermissionHolder;
import com.enthusiasm.plurelogger.command.ICommand;

public class TeleportCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("tp")
                .requires(serverCommandSource -> PermissionHolder.checkPermission(serverCommandSource, PermissionHolder.LOGGER_SPY))
                .then(
                        argument("world", DimensionArgumentType.dimension())
                                .then(
                                        argument("location", Vec3ArgumentType.vec3())
                                                .executes(context -> teleport(
                                                        context,
                                                        DimensionArgumentType.getDimensionArgument(context, "world"),
                                                        Vec3ArgumentType.getPosArgument(context, "location")
                                                ))
                                )
                )
                .build();
    }

    private int teleport(CommandContext<ServerCommandSource> context, ServerWorld world, PosArgument posArg) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var player = source.getPlayerOrThrow();
        var pos = posArg.toAbsolutePos(source);
        player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());

        return 1;
    }
}
