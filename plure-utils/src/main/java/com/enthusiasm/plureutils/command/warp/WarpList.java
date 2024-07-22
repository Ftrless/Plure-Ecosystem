package com.enthusiasm.plureutils.command.warp;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextJoiner;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarpList implements Command<ServerCommandSource> {
    private static final String GLOBAL_HEADER_TRANSLATION = "cmd.warp.list.global.header.feedback";
    private static final String PLAYER_HEADER_TRANSLATION = "cmd.warp.list.player.header.feedback";

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();
        UUID playerUUID = context.getSource().getPlayerOrThrow().getUuid();

        Message listEmpty = TextUtils.translation("cmd.warp.list.error.empty", FormatUtils.Colors.ERROR);

        List<Map.Entry<String, WarpData>> globalWarps = warpDataManager.globalWarps();
        List<Map.Entry<String, WarpData>> playerWarps = warpDataManager.playerWarps(playerUUID);
        List<Map.Entry<String, WarpData>> invitedWarps = warpDataManager.invitedWarps(playerUUID);

        if (globalWarps.isEmpty() && playerWarps.isEmpty() && invitedWarps.isEmpty()) {
            throw CommandHelper.createException(listEmpty);
        }

        List<Map.Entry<String, WarpData>> combinedWarps = new ArrayList<>(playerWarps);
        combinedWarps.addAll(invitedWarps);

        sendWarps(context, globalWarps, listEmpty, playerWarps.isEmpty() && invitedWarps.isEmpty());
        sendCombinedWarps(context, combinedWarps, invitedWarps);

        return SINGLE_SUCCESS;
    }

    private void sendWarps(CommandContext<ServerCommandSource> context, List<Map.Entry<String, WarpData>> warps,
                           Message listEmpty, boolean throwErrorIfEmpty) throws CommandSyntaxException {
        if (warps.isEmpty()) {
            if (throwErrorIfEmpty) {
                throw CommandHelper.createException(listEmpty);
            }

            return;
        }

        MutableText warpText = warps
                .stream()
                .map(warp -> createWarpText(warp.getKey(), "",false))
                .collect(TextJoiner.collector(
                        TextUtils.translation(" ", FormatUtils.Colors.DEFAULT),
                        TextUtils.translation("", FormatUtils.Colors.DEFAULT),
                        TextUtils.translation(",", FormatUtils.Colors.DEFAULT)
                ));

        MutableText header = TextUtils.translation(WarpList.GLOBAL_HEADER_TRANSLATION, FormatUtils.Colors.DEFAULT);
        context.getSource().sendFeedback(() -> header.append(warpText), false);
    }

    private void sendCombinedWarps(CommandContext<ServerCommandSource> context, List<Map.Entry<String, WarpData>> combinedWarps,
                                   List<Map.Entry<String, WarpData>> invitedWarps) throws CommandSyntaxException {
        if (invitedWarps.isEmpty() && combinedWarps.isEmpty()) {
            return;
        }

        MutableText warpText = combinedWarps
                .stream()
                .map(warp -> createWarpText(warp.getKey(),
                        CacheService.getUserByUUID(warp.getValue().owner).orElse(null),
                        invitedWarps.contains(warp))
                )
                .collect(TextJoiner.collector(
                        TextUtils.translation(" ", FormatUtils.Colors.DEFAULT),
                        TextUtils.translation("", FormatUtils.Colors.DEFAULT),
                        TextUtils.translation(",", FormatUtils.Colors.DEFAULT)
                ));

        MutableText header = TextUtils.translation(WarpList.PLAYER_HEADER_TRANSLATION, FormatUtils.Colors.DEFAULT);
        context.getSource().sendFeedback(() -> header.append(warpText), false);
    }

    private MutableText createWarpText(String warpName, String ownerName, boolean isPlayerWarp) {
        FormatUtils.Colors textColor = isPlayerWarp ? FormatUtils.Colors.SUCCESS : FormatUtils.Colors.FOCUS;
        String messageKey = isPlayerWarp ? "cmd.warp.list.invited_hover" : "cmd.warp.list.hover";

        return TextUtils.translation("cmd.warp.list.element", textColor, warpName)
                .styled(style ->
                        style
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/warp tp " + warpName
                                ))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        TextUtils.translation(messageKey, FormatUtils.Colors.DEFAULT, ownerName)
                                ))
                );
    }
}
