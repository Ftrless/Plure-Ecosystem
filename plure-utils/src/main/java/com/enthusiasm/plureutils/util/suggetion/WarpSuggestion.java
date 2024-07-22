package com.enthusiasm.plureutils.util.suggetion;

import com.enthusiasm.plurecore.utils.SuggestionUtils;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class WarpSuggestion {
    public static final SuggestionProvider<ServerCommandSource> LIST_SUGGESTION_PROVIDER = SuggestionUtils.ofContext((context)
            -> mergeWarps(context).stream()
            .map(Map.Entry::getKey)
            .toList());

    public static final SuggestionProvider<ServerCommandSource> PERM_LIST_SUGGESTION_PROVIDER = SuggestionUtils.ofContext((context)
            -> DataManager.getWarpDataManager().listWarps().keySet().stream()
            .toList());

    public static final SuggestionProvider<ServerCommandSource> WARP_TYPE = SuggestionUtils.of(() -> List.of("private", "public"));

    private static List<Map.Entry<String, WarpData>> mergeWarps(CommandContext<ServerCommandSource> context) {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();
        UUID playerUUID = context.getSource().getPlayer().getUuid();

        List<Map.Entry<String, WarpData>> mergedWarps = new ArrayList<>();
        mergedWarps.addAll(warpDataManager.globalWarps());
        mergedWarps.addAll(warpDataManager.playerWarps(playerUUID));
        mergedWarps.addAll(warpDataManager.invitedWarps(playerUUID));

        return mergedWarps;
    }
}
