package com.enthusiasm.plureutils.mixin.vanish.interaction;

import com.enthusiasm.plureutils.service.VanishService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
