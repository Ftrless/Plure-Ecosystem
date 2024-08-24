package com.enthusiasm.plureutils.util.suggetion;

import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.utils.SuggestionUtils;

public class WorldSuggestion {
    public static final SuggestionProvider<ServerCommandSource> WORLDS_SUGGESTION_PROVIDER = SuggestionUtils.ofContext((ctx) ->
            ctx.getSource().getServer().getWorldRegistryKeys().stream()
                    .map(worldRegistryKey -> worldRegistryKey.getValue().toString())
                    .toList()
    );
}
