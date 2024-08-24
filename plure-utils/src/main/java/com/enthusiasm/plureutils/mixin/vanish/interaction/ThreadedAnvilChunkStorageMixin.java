package com.enthusiasm.plureutils.mixin.vanish.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    @WrapOperation(
            method = "canTickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"
            )
    )
    public boolean vanish_preventMobSpawning(ServerPlayerEntity player, Operation<Boolean> original) {
        return original.call(player) || VanishService.isVanished(player);
    }
}
