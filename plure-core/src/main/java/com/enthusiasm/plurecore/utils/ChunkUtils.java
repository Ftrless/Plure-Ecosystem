package com.enthusiasm.plurecore.utils;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ChunkUtils {
    @Nullable
    private static ChunkHolder getChunkHolder(ServerChunkManager chunkManager, int chunkX, int chunkZ) {
        return chunkManager.getChunkHolder(ChunkPos.toLong(chunkX, chunkZ));
    }

    public static CompletableFuture<WorldChunk> loadChunkAsync(ServerWorld world, int chunkX, int chunkZ) {
        final ServerChunkManager serverChunkManager = world.getChunkManager();

        return ThreadUtils.runAsync(() -> serverChunkManager.getWorldChunk(chunkX, chunkZ, true));
    }
}
