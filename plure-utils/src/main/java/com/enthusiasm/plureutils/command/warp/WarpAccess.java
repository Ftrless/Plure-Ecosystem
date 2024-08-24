package com.enthusiasm.plureutils.command.warp;

import java.util.HashMap;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;

import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextJoiner;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;

public class WarpAccess implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();
        HashMap<String, WarpData> warpList = warpDataManager.listWarps();

        Message emptyList = TextUtils.translation("cmd.warp.list.error.empty", FormatUtils.Colors.ERROR);

        if (warpList.isEmpty()) {
            throw CommandHelper.createException(emptyList);
        }

        MutableText warpText = warpList
                .keySet()
                .stream()
                .map(this::createWarpText)
                .collect(TextJoiner.collector(
                        TextUtils.translation(" ", FormatUtils.Colors.DEFAULT),
                        TextUtils.translation("", FormatUtils.Colors.DEFAULT),
                        TextUtils.translation(",", FormatUtils.Colors.DEFAULT)
                ));

        MutableText header = TextUtils.translation("cmd.warp.list.global.header.feedback", FormatUtils.Colors.DEFAULT);
        context.getSource().sendFeedback(() -> header.append(warpText), false);
    }

    private MutableText createWarpText(String warpName) {
        return TextUtils.translation("cmd.warp.list.element", FormatUtils.Colors.FOCUS, warpName)
                .styled(style ->
                        style
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/warp tp " + warpName
                                ))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        TextUtils.translation("cmd.warp.list.hover", FormatUtils.Colors.DEFAULT)
                                ))
                );
    }
}
