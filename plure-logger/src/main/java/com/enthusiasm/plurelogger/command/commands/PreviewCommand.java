package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.CacheService;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.command.CommandConstants;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.command.arguments.SearchParamArgument;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;

public class PreviewCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("preview")
                .then(literal("rollback")
                        .then(SearchParamArgument.argument(CommandConstants.PARAMS)
                                .executes(context -> preview(
                                        context,
                                        SearchParamArgument.get(context, CommandConstants.PARAMS),
                                        Preview.Type.ROLLBACK
                                ))
                        )
                )
                .then(literal("restore")
                        .then(SearchParamArgument.argument(CommandConstants.PARAMS)
                                .executes(context -> preview(
                                        context,
                                        SearchParamArgument.get(context, CommandConstants.PARAMS),
                                        Preview.Type.RESTORE
                                ))
                        )
                )
                .then(literal("apply")
                        .executes(this::apply)
                )
                .then(literal("cancel")
                        .executes(this::cancel)
                )
                .build();
    }

    private int preview(CommandContext<ServerCommandSource> context, ActionSearchParams params, Preview.Type type) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var player = source.getPlayerOrThrow();
        params.ensureSpecific();

        ThreadUtils.runAsync(() -> {
           //TODO: send busy
            var actions = DatabaseService.previewActions(params, type);

            if (actions.isEmpty()) {
                source.sendError(Text.translatable("error.command.no_results"));
                return;
            }

            UUID playerUuid = player.getUuid();
            CacheService.getPreviewCache().getOrDefault(playerUuid, null).cancel(player);
            CacheService.getPreviewCache().put(playerUuid, new Preview(params, actions, player, type));
        });

        return 1;
    }

    private int apply(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        UUID uuid = context.getSource().getPlayerOrThrow().getUuid();

        if (CacheService.getPreviewCache().containsKey(uuid)) {
            CacheService.getPreviewCache().get(uuid).apply(context);
            CacheService.getPreviewCache().remove(uuid);
        } else {
            context.getSource().sendError(Text.translatable("error.no_preview"));
            return -1;
        }

        return 1;
    }

    private int cancel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        UUID uuid = context.getSource().getPlayerOrThrow().getUuid();

        if (CacheService.getPreviewCache().containsKey(uuid)) {
            CacheService.getPreviewCache().get(uuid).cancel(context.getSource().getPlayerOrThrow());
            CacheService.getPreviewCache().remove(uuid);
        } else {
            context.getSource().sendError(Text.translatable("error.no_preview"));
            return -1;
        }

        return 1;
    }
}
