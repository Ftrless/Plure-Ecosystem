package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

import com.enthusiasm.plurelogger.listener.events.EntityKillEvent;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    @Inject(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private void villagerToWitch(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EntityKillEvent.invoke(entity.getWorld(), entity.getBlockPos(), entity, world.getDamageSources().lightningBolt());
    }
}
