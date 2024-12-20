package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.HashMap;
import java.util.HashSet;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.command.arguments.SearchParamArgument;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class RestoreCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("restore")
                .then(literal("params")
                        .then(SearchParamArgument.argument("params")
                                .executes(context -> restore(context, SearchParamArgument.get(context, "params")))
                        )
                )
                .build();
    }

    public int restore(CommandContext<ServerCommandSource> context, ActionSearchParams params) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        params.ensureSpecific();

        ThreadUtils.runAsync(() -> {
            //TODO: warn busy
            var actions = DatabaseService.selectRestore(params);

            if (actions.isEmpty()) {
                source.sendError(Text.translatable("error.command.no_results"));
                return;
            }

            source.sendFeedback(() ->
                    Text.translatable(
                            "text.restore.start",
                            Text.literal(String.valueOf(actions.size())).setStyle(TextColorPallet.getSecondary())
                    ).setStyle(TextColorPallet.getPrimary()),
                    true
            );

            context.getSource().getWorld().getServer().execute(() -> {
                HashMap<String, Integer> fails = new HashMap<>();
                HashSet<Integer> actionIds = new HashSet<>();

                actions.forEach(action -> {
                    if (!action.restore(context.getSource().getServer())) {
                        fails.put(action.getIdentifier(), fails.getOrDefault(action.getIdentifier(), 0) + 1);
                    } else {
                        actionIds.add(action.getId());
                    }
                });

                ThreadUtils.runAsync(() -> DatabaseService.restoreActions(actionIds));

                fails.forEach((key, value) -> source.sendFeedback(() ->
                        Text.translatable("text.restore.fail", key, value)
                                .setStyle(TextColorPallet.getSecondary()),
                        true
                ));

                source.sendFeedback(() ->
                        Text.translatable("text.restore.finish", actions.size())
                                .setStyle(TextColorPallet.getPrimary()),
                        true
                );
            });
        });

        return 1;
    }
}
