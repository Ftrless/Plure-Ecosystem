package com.enthusiasm.plureutils.command.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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

public class WarpInvite implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);
        String warpName = StringArgumentType.getString(context, "warp_name");

        exec(context, senderPlayer, targetPlayer, warpName);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer,
                    ServerPlayerEntity targetPlayer, String warpName) throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();

        Message notExist = TextUtils.translation("cmd.warp.invite.error.not-exist", FormatUtils.Colors.ERROR);
        Message notOwner = TextUtils.translation("cmd.warp.invite.error.not-owner", FormatUtils.Colors.ERROR);
        Message selfInvitation = TextUtils.translation("cmd.warp.invite.error.self-invitation", FormatUtils.Colors.ERROR);

        WarpData warpData = warpDataManager.getWarp(warpName);

        if (warpData == null) {
            throw CommandHelper.createException(notExist);
        }

        if (!warpData.owner.equals(senderPlayer.getUuid())
                && !PermissionsHolder.check(senderPlayer, PermissionsHolder.Permission.BYPASS_TYPE_WARP, 4)) {
            throw CommandHelper.createException(notOwner);
        }

        if (senderPlayer.equals(targetPlayer)) {
            throw CommandHelper.createException(selfInvitation);
        }

        warpDataManager.invitePlayer(warpName, targetPlayer.getUuid());

        PlayerUtils.sendFeedback(context, "cmd.warp.invite.feedback", warpName);
    }
}
