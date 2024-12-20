package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.CacheService;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.MessageUtils;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class PageCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("page")
                .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> page(context, IntegerArgumentType.getInteger(context, "page"))))
                .build();
    }

    private int page(CommandContext<ServerCommandSource> context, int page) {
        var source = context.getSource();

        var params = CacheService.getSearchCache().get(source.getName());

        if (params != null) {
            ThreadUtils.runAsync(() -> {
                var results = DatabaseService.searchActions(params, page);

                if (results.page() > results.pages()) {
                    source.sendError(Text.translatable("error.no_more_pages"));
                    return;
                }

                MessageUtils.sendSearchResults(
                        source,
                        results,
                        Text.translatable("text.header.search").setStyle(TextColorPallet.getPrimary())
                );
            });

            return 1;
        } else {
            source.sendError(Text.translatable("error.no_cached_params"));
            return -1;
        }
    }
}
