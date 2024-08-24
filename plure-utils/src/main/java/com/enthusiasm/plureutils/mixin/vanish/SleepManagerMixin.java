package com.enthusiasm.plureutils.mixin.vanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(SleepManager.class)
public abstract class SleepManagerMixin {
    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"
            )
    )
    public boolean vanish_hideSleeping(ServerPlayerEntity player, Operation<Boolean> original) {
        return original.call(player) || VanishService.isVanished(player);
    }
}
