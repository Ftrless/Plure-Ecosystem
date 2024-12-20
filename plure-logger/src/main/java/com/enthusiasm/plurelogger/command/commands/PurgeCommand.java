package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.command.CommandConstants;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.command.arguments.SearchParamArgument;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class PurgeCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("purge")
                .then(literal("params")
                        .then(SearchParamArgument.argument(CommandConstants.PARAMS)
                                .executes(context -> runPurge(context, SearchParamArgument.get(context, CommandConstants.PARAMS)))
                        )
                )
                .build();
    }

    private int runPurge(CommandContext<ServerCommandSource> ctx, ActionSearchParams params) {
        ServerCommandSource source = ctx.getSource();
        source.sendFeedback(() ->
                Text.translatable("text.purge.starting").setStyle(TextColorPallet.getSecondary()),
                true
        );

        ThreadUtils.runAsync(() -> {
            DatabaseService.purgeActions(params);
            source.sendFeedback(() ->
                    Text.translatable("text.purge.complete").setStyle(TextColorPallet.getPrimary()),
                    true
            );
        });

        return 1;
    }
}
