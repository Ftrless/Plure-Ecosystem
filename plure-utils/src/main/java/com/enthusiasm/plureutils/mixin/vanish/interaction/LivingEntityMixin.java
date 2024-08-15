package com.enthusiasm.plureutils.mixin.vanish.interaction;

import com.enthusiasm.plureutils.service.VanishService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
            method = "isPushable",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isSpectator()Z"
            )
    )
    public boolean vanish_preventPushing(LivingEntity entity, Operation<Boolean> original) {
        return original.call(entity) || VanishService.isVanished(entity);
    }
}