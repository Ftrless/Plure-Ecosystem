package com.enthusiasm.plurecore.utils.suggestion;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.SuggestionUtils;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

public class NickSuggestionProvider {
    public static final SuggestionProvider<ServerCommandSource> PROVIDER = SuggestionUtils.of(CacheService::getCachedUsernames);

}
