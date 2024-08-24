package com.enthusiasm.plurecore.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.cache.CacheService;

public class PlayerEvents {
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(PlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();

        if (CacheService.userExists(player.getUuid())) {
            return;
        }

        CacheService.addOrUpdateUser(player.getUuid(), player.getEntityName());
    }
}
