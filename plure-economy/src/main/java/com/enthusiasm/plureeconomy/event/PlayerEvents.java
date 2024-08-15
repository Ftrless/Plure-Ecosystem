package com.enthusiasm.plureeconomy.event;

import com.enthusiasm.plureeconomy.api.EconomyAPI;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEvents {
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(PlayerEvents::onPlayerConnect);
    }

    private static void onPlayerConnect(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();

        EconomyAPI
                .checkPlayerExists(player)
                .thenAcceptAsync(result -> {
                    if (!result) {
                        EconomyAPI.savePlayer(player);
                    }
                });
    }
}
