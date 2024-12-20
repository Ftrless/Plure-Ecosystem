package com.enthusiasm.plurelogger.command.parameters;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lombok.SneakyThrows;

import net.minecraft.command.CommandSource;

import com.enthusiasm.plurelogger.registry.ActionRegistry;

public class ActionParameter extends AbstractParameter<String> {
    @SneakyThrows
    @Override
    public String parse(StringReader reader) {
        return StringArgumentType.word().parse(reader);
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ObjectSet<String> actions = ActionRegistry.getTypes();

        return CommandSource.suggestMatching(actions, builder);
    }
}
