package com.enthusiasm.plureutils.command.warp;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.WorldUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class WarpInfo implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String warpName = StringArgumentType.getString(context, "warp_name");

        exec(context, warpName);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, String warpName) throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();
        MinecraftServer server = context.getSource().getServer();

        WarpData warpData = warpDataManager.getWarp(warpName);

        Message notExist = TextUtils.translation("cmd.warp.info.error.not-exist", FormatUtils.Colors.ERROR);
        Message publicType = TextUtils.translation("cmd.warp.info.type.public", FormatUtils.Colors.SUCCESS);
        Message privateType = TextUtils.translation("cmd.warp.info.type.private", FormatUtils.Colors.ERROR);

        if (warpData == null) {
            throw CommandHelper.createException(notExist);
        }

        String world = WorldUtils.findRegistryKey(warpData.world, server, false).getValue().toString();

        PlayerUtils.sendFeedback(
                context,
                "cmd.warp.info.feedback",
                warpName,
                CacheService.getUserByUUID(warpData.owner).orElse(null),
                warpData.global ? publicType : privateType,
                "x: " + Math.floor(warpData.x) + ", y: " + Math.floor(warpData.y) + ", z: " + Math.floor(warpData.z),
                world,
                0
        );
    }
}
