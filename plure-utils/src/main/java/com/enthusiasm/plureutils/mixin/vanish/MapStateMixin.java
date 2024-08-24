package com.enthusiasm.plureutils.mixin.vanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.map.MapState;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(MapState.class)
public abstract class MapStateMixin {
    @Shadow
    protected abstract void removeIcon(String string);

    @WrapOperation(
            method = "update",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/item/map/MapState;showIcons:Z",
                    ordinal = 0
            )
    )
    private boolean vanish_hideFromMap(MapState instance, Operation<Boolean> original, @Local MapState.PlayerUpdateTracker holdingPlayer) {
        if (VanishService.isVanished(holdingPlayer.player)) {
            removeIcon(holdingPlayer.player.getName().getString());
            return false;
        } else {
            return original.call(instance);
        }
    }
}
