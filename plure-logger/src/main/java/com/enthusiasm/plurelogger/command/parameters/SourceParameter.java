package com.enthusiasm.plurelogger.command.parameters;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;

public class SourceParameter extends AbstractParameter<String> {
    @Override
    public String parse(StringReader reader) {
        int i = reader.getCursor();

        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        return reader.getString().substring(i, reader.getCursor());
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());

        Set<String> sources = new HashSet<>(context.getSource().getPlayerNames());
        DatabaseService.getCache().sourceKeys.asMap().keySet().forEach(source -> sources.add("@" + source));

        return CommandSource.suggestMatching(sources, builder);
    }
}
