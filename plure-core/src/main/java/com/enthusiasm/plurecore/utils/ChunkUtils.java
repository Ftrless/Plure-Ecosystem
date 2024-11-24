package com.enthusiasm.plurecore.utils;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import com.enthusiasm.plurecore.mixin.ServerChunkManagerAccessor;

public class ChunkUtils {
    @Nullable
    public static WorldChunk getChunkIfLoaded(ServerWorld level, int chunkX, int chunkZ) {
        final ChunkHolder holder = getChunkHolder(level.getChunkManager(), chunkX, chunkZ);

        return holder != null ? holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().orElse(null) : null;
    }

    @Nullable
    private static ChunkHolder getChunkHolder(ServerChunkManager chunkCache, int chunkX, int chunkZ) {
        ServerChunkManagerAccessor accessor = (ServerChunkManagerAccessor) chunkCache;
        return accessor.getHolder(ChunkPos.toLong(chunkX, chunkZ));
    }

    public static CompletableFuture<WorldChunk> loadChunkAsync(ServerWorld world, int chunkX, int chunkZ) {
        final ServerChunkManager serverChunkManager = world.getChunkManager();

        return ThreadUtils.runAsync(() -> serverChunkManager.getWorldChunk(chunkX, chunkZ, true));
    }
}
