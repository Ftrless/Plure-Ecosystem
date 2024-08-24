package com.enthusiasm.plureutils.mixin.vanish;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
    private boolean vanish_invulnerablePlayers(boolean original) {
        if (VanishService.isVanished((PlayerEntity) (Object) this)) {
            return true;
        }

        return original;
    }
}
