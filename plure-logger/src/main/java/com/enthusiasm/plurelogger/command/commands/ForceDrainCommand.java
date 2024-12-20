package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;

public class ForceDrainCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("drain")
                .executes(this::runDrain)
                .build();
    }

    public int runDrain(CommandContext<ServerCommandSource> ctx) {
        ActionQueueService.drainBatch();

        return 1;
    }
}
