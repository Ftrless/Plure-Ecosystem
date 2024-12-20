package com.enthusiasm.plurelogger.command.parameters;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.SneakyThrows;

import net.minecraft.server.command.ServerCommandSource;

public class RollbackStatusParameter extends AbstractParameter<Boolean> {
    @SneakyThrows
    @Override
    public Boolean parse(StringReader reader) {
        return BoolArgumentType.bool().parse(reader);
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        return BoolArgumentType.bool().listSuggestions(context, builder);
    }
}
