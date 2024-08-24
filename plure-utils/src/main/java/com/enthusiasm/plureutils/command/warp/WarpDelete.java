package com.enthusiasm.plureutils.command.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.PermissionsHolder;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;

public class WarpDelete implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();

        String warpName = StringArgumentType.getString(context, "warp_name");
        WarpData warpData = warpDataManager.getWarp(warpName);

        Message notExist = TextUtils.translation("cmd.warp.delete.error.not-exist", FormatUtils.Colors.ERROR);
        Message notOwner = TextUtils.translation("cmd.warp.delete.error.not-owner", FormatUtils.Colors.ERROR);

        if (warpData == null) {
            throw CommandHelper.createException(notExist);
        }

        if (!warpData.owner.equals(senderPlayer.getUuid())
                && !PermissionsHolder.check(senderPlayer, PermissionsHolder.Permission.BYPASS_TYPE_WARP, 4)) {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(notOwner), notOwner);
        }

        warpDataManager.deleteWarp(warpName);

        PlayerUtils.sendFeedback(context, "cmd.warp.delete.feedback", warpName);
    }
}
