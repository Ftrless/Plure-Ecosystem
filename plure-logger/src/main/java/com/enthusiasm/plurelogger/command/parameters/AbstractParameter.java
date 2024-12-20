package com.enthusiasm.plurelogger.command.parameters;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.server.command.ServerCommandSource;

public abstract class AbstractParameter<T> implements SuggestionProvider<ServerCommandSource> {
    public abstract T parse(StringReader reader);
}
