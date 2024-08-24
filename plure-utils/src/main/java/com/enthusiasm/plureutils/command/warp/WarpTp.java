package com.enthusiasm.plureutils.command.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.PermissionsHolder;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.warp.WarpData;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;

public class WarpTp implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer)
            throws CommandSyntaxException {
        WarpDataManager warpDataManager = DataManager.getWarpDataManager();

        String warpName = StringArgumentType.getString(context, "warp_name");
        WarpData warpData = warpDataManager.getWarp(warpName);

        MutableText notExists = TextUtils.translation("cmd.warp.tp.error.not-found", FormatUtils.Colors.ERROR);

        if (warpData == null) {
            throw CommandHelper.createException(notExists);
        }

        if ((!warpData.global && !senderPlayer.getUuid().equals(warpData.owner)) &&
                !PermissionsHolder.check(senderPlayer, PermissionsHolder.Permission.BYPASS_TYPE_WARP, 4)) {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(
                    TextUtils.translation("cmd.warp.tp.error.not-found", FormatUtils.Colors.ERROR)),
                    TextUtils.translation("cmd.warp.tp.error.not-found", FormatUtils.Colors.ERROR));
        }

        ServerWorld targetWorld = findWorld(senderPlayer.getServer(), warpData.world);

        PlayerUtils.teleportPlayer(senderPlayer, warpData.x, warpData.y, warpData.z, warpData.yaw, warpData.pitch, targetWorld);

        PlayerUtils.sendFeedback(context, "cmd.warp.tp.feedback", warpName);
    }

    private ServerWorld findWorld(MinecraftServer server, String worldName) {
        var registryKeyWorld = server.getWorldRegistryKeys().stream()
                .filter(world -> world.toString().equals(worldName))
                .findFirst()
                .orElse(null);

        return server.getWorld(registryKeyWorld);
    }
}
