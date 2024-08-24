package com.enthusiasm.plureutils.mixin.vanish.interaction;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @WrapWithCondition(
            method = "collideWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;onPlayerCollision(Lnet/minecraft/entity/player/PlayerEntity;)V"
            )
    )
    private boolean vanish_preventPickup(Entity entity, PlayerEntity player) {
        return !VanishService.isVanished(player);
    }

    @ModifyReturnValue(
            method = "canBeHitByProjectile",
            at = @At("RETURN")
    )
    public boolean vanish_preventProjectileHits(boolean original) {
        if (original) {
            return !VanishService.isVanished((PlayerEntity) (Object) this);
        }

        return false;
    }
}
