package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.PermissionHolder;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.utils.InspectionUtils;

public class InspectCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("inspect")
                .requires(serverCommandSource -> PermissionHolder.checkPermission(serverCommandSource, PermissionHolder.LOGGER_SPY))
                        .executes(this::toggleInspect)
                        .then(
                                literal("on")
                                        .executes(context -> InspectionUtils.inspectOn(context.getSource().getPlayerOrThrow()))
                        )
                        .then(
                                literal("off")
                                        .executes(context -> InspectionUtils.inspectOff(context.getSource().getPlayerOrThrow()))
                        )
                        .then(
                                argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(context -> inspectBlock(context, BlockPosArgumentType.getBlockPos(context, "pos")))
                        )
                        .build();
    }

    private int toggleInspect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var player = source.getPlayerOrThrow();

        if (InspectionUtils.isInspecting(player)) {
            InspectionUtils.inspectOff(player);
        } else {
            InspectionUtils.inspectOn(player);
        }

        return 1;
    }

    private int inspectBlock(CommandContext<ServerCommandSource> context, BlockPos pos) {
        ServerCommandSource source = context.getSource();

        InspectionUtils.inspectBlock(source, pos);
        return 1;
    }
}
