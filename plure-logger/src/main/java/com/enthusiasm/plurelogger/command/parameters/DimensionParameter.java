package com.enthusiasm.plurelogger.command.parameters;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.SneakyThrows;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class DimensionParameter extends AbstractParameter<Identifier> {
    @SneakyThrows
    @Override
    public Identifier parse(StringReader reader) {
        return DimensionArgumentType.dimension().parse(reader);
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        return DimensionArgumentType.dimension().listSuggestions(context, builder);
    }
}
