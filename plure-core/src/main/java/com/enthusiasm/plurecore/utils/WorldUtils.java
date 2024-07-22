package com.enthusiasm.plurecore.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class WorldUtils {
    public static RegistryKey<World> findWorldKey(String worldName, MinecraftServer server) {
        return server.getWorldRegistryKeys().stream()
                .filter(world -> world.toString().equals(worldName))
                .findFirst()
                .orElse(null);
    }

    public static ServerWorld getServerWorld(String worldName, MinecraftServer server) {
        return server.getWorld(findWorldKey(worldName, server));
    }
}
