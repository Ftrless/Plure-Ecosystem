package com.enthusiasm.plurelogger.command.parameters;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.SneakyThrows;

import net.minecraft.server.command.ServerCommandSource;

public class RangeParameter extends AbstractParameter<Integer> {
    private static final int MAX_SIZE = 9;

    @SneakyThrows
    @Override
    public Integer parse(StringReader reader) {
        return IntegerArgumentType.integer(1).parse(reader);
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var remaining = builder.getRemaining().toLowerCase().trim();

        for (int i = 0; i < MAX_SIZE; i++) {
            builder.suggest(remaining + i);
        }

        return builder.buildFuture();
    }
}
