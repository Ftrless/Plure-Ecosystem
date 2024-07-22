package com.enthusiasm.plureutils.util.suggetion;

import com.enthusiasm.plurecore.utils.SuggestionUtils;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class VoteSuggestion {
    public static final SuggestionProvider<ServerCommandSource> LIST_SUGGESTION_PROVIDER = SuggestionUtils.ofContext((ctx) -> List.of("day", "sun"));
}
