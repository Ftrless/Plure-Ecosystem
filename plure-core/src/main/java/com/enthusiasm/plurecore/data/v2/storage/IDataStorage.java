package com.enthusiasm.plurecore.data.v2.storage;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IDataStorage<T> {
    default boolean save(ServerPlayerEntity player, T settings) {
        return this.save(player.server, player.getUuid(), settings);
    }

    boolean save(MinecraftServer server, UUID player, T settings);

    @Nullable
    default T load(ServerPlayerEntity player) {
        return this.load(player.server, player.getUuid());
    }
    @Nullable
    T load(MinecraftServer server, UUID player);
}
