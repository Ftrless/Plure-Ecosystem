package com.enthusiasm.plureutils.event;

import com.enthusiasm.plureutils.config.ConfigManager;
import com.enthusiasm.plureutils.service.AutowipeService;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerEvents {
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(PlayerEvents::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerEvents::onPlayerDisconnect);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(PlayerEvents::onPlayerChangeWorld);
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        ServerWorld world = player.getServerWorld();
        String worldId = world.getRegistryKey().getValue().toString();

        if (ConfigManager.getConfig().specialWorlds.contains(worldId)) {
            AutowipeService.AUTO_WIPE_BOSS_BAR.addPlayer(player);
            return;
        }

        if (AutowipeService.AUTO_WIPE_BOSS_BAR.getPlayers().contains(player)) {
            AutowipeService.AUTO_WIPE_BOSS_BAR.removePlayer(player);
        }
    }

    private static void onPlayerDisconnect(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();

        if (AutowipeService.AUTO_WIPE_BOSS_BAR.getPlayers().contains(player)) {
            AutowipeService.AUTO_WIPE_BOSS_BAR.removePlayer(player);
        }
    }

    private static void onPlayerChangeWorld(ServerPlayerEntity player, ServerWorld worldFrom, ServerWorld worldTo) {
        String worldIdFrom = worldFrom.getRegistryKey().getValue().toString();
        String worldIdTo = worldTo.getRegistryKey().getValue().toString();

        if (ConfigManager.getConfig().specialWorlds.contains(worldIdTo)) {
            AutowipeService.AUTO_WIPE_BOSS_BAR.addPlayer(player);
            return;
        }

        if (ConfigManager.getConfig().specialWorlds.contains(worldIdFrom)) {
            AutowipeService.AUTO_WIPE_BOSS_BAR.removePlayer(player);
        }
    }
}
