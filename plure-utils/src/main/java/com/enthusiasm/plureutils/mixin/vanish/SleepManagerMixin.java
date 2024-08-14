package com.enthusiasm.plureutils.mixin.vanish;

import com.enthusiasm.plureutils.service.VanishService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
