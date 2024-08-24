package com.enthusiasm.plureutils.util.suggetion;

import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.SuggestionUtils;


public class NickSuggestion {
    public static final SuggestionProvider<ServerCommandSource> NICK_SUGGESTION_PROVIDER = SuggestionUtils.of(CacheService::getCachedUsernames);
}
