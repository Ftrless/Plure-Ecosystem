package com.enthusiasm.plureutils.util.suggetion;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.SuggestionUtils;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

public class NickSuggestion {
    public static final SuggestionProvider<ServerCommandSource> NICK_SUGGESTION_PROVIDER = SuggestionUtils.of(CacheService::getCachedUsernames);
}
