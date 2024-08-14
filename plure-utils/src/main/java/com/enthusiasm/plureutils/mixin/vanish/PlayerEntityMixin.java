package com.enthusiasm.plureutils.mixin.vanish;

import com.enthusiasm.plureutils.service.VanishService;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
