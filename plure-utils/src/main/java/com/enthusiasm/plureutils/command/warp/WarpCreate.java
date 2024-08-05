package com.enthusiasm.plureutils.command.warp;

import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;

public class WarpCreate implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();

        exec(context, targetPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();

        String warpName = StringArgumentType.getString(context, "warp_name");
        String global = StringArgumentType.getString(context, "global");

        Message alreadyExist = TextUtils.translation("cmd.warp.create.error.exist", FormatUtils.Colors.ERROR);
        WarpData warpData = warpDataManager.getWarp(warpName);

        if (warpData != null) {
            throw CommandHelper.createException(alreadyExist);
        }

        if (global.equals("public")) {
            return;
        }

        warpDataManager.addWarp(warpName, new WarpData(
                senderPlayer.getWorld().getRegistryKey().toString(),
                senderPlayer.getUuid(),
                senderPlayer.getX(),
                senderPlayer.getY(),
                senderPlayer.getZ(),
                senderPlayer.getYaw(),
                senderPlayer.getPitch(),
                global.equals("public"),
                0, Collections.emptyList())
        );

        PlayerUtils.sendFeedback(context, "cmd.warp.create.feedback", warpName);
    }
}
