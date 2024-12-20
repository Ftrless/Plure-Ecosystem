package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class StatusCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("status")
                .executes(this::status)
                .build();
    }

    private int status(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() ->
                        Text.translatable("text.header.status")
                                .setStyle(TextColorPallet.getPrimary()),
                false
        );

        source.sendFeedback(() ->
                        Text.translatable(
                                "text.status.queue",
                                Text.literal(String.valueOf(ActionQueueService.getSize()))
                                        .setStyle(TextColorPallet.getSecondaryVariant())
                        ).setStyle(TextColorPallet.getSecondary()),
                false
        );

        return 1;
    }
}
