package com.enthusiasm.plurecore.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class WorldUtils {
    public static ServerWorld getServerWorld(String worldName, MinecraftServer server) {
        return getServerWorld(worldName, server, false);
    }

    public static ServerWorld getServerWorld(String worldName, MinecraftServer server, boolean byValue) {
        return server.getWorld(findRegistryKey(worldName, server, byValue));
    }

    public static RegistryKey<World> findRegistryKey(String worldName, MinecraftServer server, boolean byValue) {
        Stream<RegistryKey<World>> worldRegistryKeys = server.getWorldRegistryKeys().stream();
        Stream<RegistryKey<World>> filteredRegistryKeys = byValue
                ? worldRegistryKeys.filter(worldRegistryKey -> worldRegistryKey.getValue().toString().equals(worldName))
                : worldRegistryKeys.filter(worldRegistryKey -> worldRegistryKey.toString().equals(worldName));

        return filteredRegistryKeys
                .findFirst()
                .orElse(null);
    }
}
