package com.enthusiasm.plureutils.mixin.vanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.EntityView;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(EntityView.class)
public class EntityViewMixin {
    @WrapOperation(
            method = "doesNotIntersectEntities",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;intersectionChecked:Z"
            )
    )
    boolean vanish_noBlockObstruction(Entity entity, Operation<Boolean> original) {
        if (entity instanceof ServerPlayerEntity serverPlayer && VanishService.isVanished(serverPlayer)) {
            return false;
        }

        return original.call(entity);
    }
}
