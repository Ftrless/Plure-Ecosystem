package com.enthusiasm.plurekits.suggestion;

import com.enthusiasm.plurecore.utils.SuggestionUtils;
import com.enthusiasm.plurekits.KitService;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;

public class KitSuggestion {
    public static final SuggestionProvider<ServerCommandSource> ALL_KITS_SUGGESTION_PROVIDER =
            SuggestionUtils.ofContext(ctx -> KitService.getAllKitsForPlayer(ctx.getSource().getPlayer())
                    .map(Map.Entry::getKey)
                    .toList()
            );
    public static final SuggestionProvider<ServerCommandSource> CLAIMABLE_KITS_SUGGESTION_PROVIDER =
            SuggestionUtils.ofContext(ctx -> KitService.getClaimableKitsForPlayer(ctx.getSource().getPlayer())
                    .map(Map.Entry::getKey)
                    .toList()
            );
}