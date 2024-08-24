package com.enthusiasm.plureutils.command.home;

import java.util.Collections;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.home.HomeData;
import com.enthusiasm.plureutils.data.home.HomeDataManager;

public class HomeSet implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();

        exec(context, targetPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        HomeDataManager homeDataManager = DataManager.getHomeDataManager();

        HomeData homeData = homeDataManager.getHome(senderPlayer.getUuid());

        PlayerUtils.sendFeedback(context, "cmd.home.set.feedback");

        if (homeData != null) {
            homeData.x = senderPlayer.getX();
            homeData.y = senderPlayer.getY();
            homeData.z = senderPlayer.getZ();
            homeData.pitch = senderPlayer.getPitch();
            homeData.yaw = senderPlayer.getYaw();

            homeDataManager.editHome(senderPlayer.getUuid(), homeData);

            return;
        }

        homeDataManager.addHome(senderPlayer.getUuid(), new HomeData(
                senderPlayer.getWorld().getRegistryKey().toString(),
                senderPlayer.getUuid(),
                senderPlayer.getX(),
                senderPlayer.getY(),
                senderPlayer.getZ(),
                senderPlayer.getYaw(),
                senderPlayer.getPitch(),
                Collections.emptyList())
        );
    }
}
