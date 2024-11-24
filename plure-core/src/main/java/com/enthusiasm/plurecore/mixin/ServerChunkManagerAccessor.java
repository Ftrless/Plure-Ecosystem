package com.enthusiasm.plurecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;

@Mixin(ServerChunkManager.class)
public interface ServerChunkManagerAccessor {
    @Invoker("getChunkHolder")
    ChunkHolder getHolder(long pos);
}
