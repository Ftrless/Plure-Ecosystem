package com.enthusiasm.plurecore.utils.suggestion;

import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.SuggestionUtils;

public class NickSuggestionProvider {
    public static final SuggestionProvider<ServerCommandSource> PROVIDER = SuggestionUtils.of(CacheService::getCachedUsernames);

}
