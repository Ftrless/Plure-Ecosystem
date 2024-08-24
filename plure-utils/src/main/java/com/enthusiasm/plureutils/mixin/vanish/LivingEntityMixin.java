package com.enthusiasm.plureutils.mixin.vanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
            method = "fall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"
            )
    )
    public <T extends ParticleEffect> int vanish_hideFallingParticles(ServerWorld world, T particleOptions, double x, double y, double z, int count, double dx, double dy, double dz, double speed, Operation<Integer> original) {
        if (!VanishService.isVanished((LivingEntity) (Object) this)) {
            return original.call(world, particleOptions, x, y, z, count, dx, dy, dz, speed);
        }

        return 0;
    }

    @Inject(method = "isPartOfGame", at = @At("HEAD"), cancellable = true)
    public void vanish_hideFromEntities(CallbackInfoReturnable<Boolean> cir) {
        if (VanishService.isVanished((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
