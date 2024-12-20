package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.CacheService;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.command.arguments.SearchParamArgument;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.MessageUtils;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class SearchCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("search")
                .then(literal("params")
                        .then(SearchParamArgument.argument("params")
                                .executes(context -> search(context, SearchParamArgument.get(context, "params")))
                        )
                )
                .build();
    }

    private int search(CommandContext<ServerCommandSource> context, ActionSearchParams params) {
        ServerCommandSource source = context.getSource();

        ThreadUtils.runAsync(() -> {
            CacheService.getSearchCache().put(source.getName(), params);

            var results = DatabaseService.searchActions(params, 1);

            if (results.actions().isEmpty()) {
                source.sendError(Text.translatable("error.command.no_results"));
                return;
            }

            MessageUtils.sendSearchResults(
                    source,
                    results,
                    Text.translatable("text.header.search")
                            .setStyle(TextColorPallet.getPrimary())
            );
        });

        return 1;
    }
}
