package com.enthusiasm.plurekits.event;

import com.enthusiasm.plurekits.data.DataManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEvents {
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(PlayerEvents::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerEvents::onPlayerQuit);
    }

    private static void onPlayerJoin(
            ServerPlayNetworkHandler networkHandler,
            PacketSender packetSender,
            MinecraftServer server
    ) {
        ServerPlayerEntity player = networkHandler.getPlayer();

        DataManager.getInstance().loadPlayerKitData(player);
    }

    private static void onPlayerQuit(
            ServerPlayNetworkHandler networkHandler,
            MinecraftServer server
    ) {
        ServerPlayerEntity player = networkHandler.getPlayer();

        DataManager.getInstance().unloadPlayerKitData(player);
    }
}
