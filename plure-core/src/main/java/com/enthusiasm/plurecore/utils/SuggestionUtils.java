package com.enthusiasm.plurecore.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SuggestionUtils {
    public static CompletableFuture<Suggestions> buildSuggestions(SuggestionsBuilder builder, Collection<String> suggestionCollection) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

        if (suggestionCollection.isEmpty()) {
            return Suggestions.empty();
        }

        for (String str : suggestionCollection) {
            if (str.toLowerCase(Locale.ROOT).startsWith(remaining)) {
                builder.suggest(str);
            }
        }

        return builder.buildFuture();
    }

    @Contract(pure = true)
    public static @NotNull SuggestionProvider<ServerCommandSource> of(Supplier<Collection<String>> suggestionCollection) {
        return (CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
                -> buildSuggestions(builder, suggestionCollection.get());
    }

    @Contract(pure = true)
    public static <S> @NotNull SuggestionProvider<S> ofContext(IContext<CommandContext<S>, Collection<String>> suggestionCollection) {
        return (CommandContext<S> context, SuggestionsBuilder builder)
                -> buildSuggestions(builder, suggestionCollection.apply(context));
    }

    @FunctionalInterface
    public interface IContext<T, R> {
        R apply(T o) throws CommandSyntaxException;
    }
}

