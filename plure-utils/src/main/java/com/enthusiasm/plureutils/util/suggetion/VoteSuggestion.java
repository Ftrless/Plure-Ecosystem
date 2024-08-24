package com.enthusiasm.plureutils.util.suggetion;

import java.util.List;

import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.utils.SuggestionUtils;

public class VoteSuggestion {
    public static final SuggestionProvider<ServerCommandSource> LIST_SUGGESTION_PROVIDER = SuggestionUtils.ofContext((ctx) -> List.of("day", "sun"));
}
