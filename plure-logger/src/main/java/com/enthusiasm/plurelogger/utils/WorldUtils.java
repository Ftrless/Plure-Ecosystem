package com.enthusiasm.plurelogger.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class WorldUtils {
    public static ServerWorld getWorld(MinecraftServer server, Identifier world) {
        return server.getWorld(RegistryKey.of(RegistryKeys.WORLD, world));
    }
}
