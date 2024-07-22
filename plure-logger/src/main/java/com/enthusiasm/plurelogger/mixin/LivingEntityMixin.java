package com.enthusiasm.plurelogger.mixin;

import com.enthusiasm.plurelogger.event.PlayerDropCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "triggerItemPickedUpByEntityCriteria", at = @At(value = "HEAD"))
    public void onPickup(ItemEntity item, CallbackInfo ci) {
        World world = item.getWorld();
        PlayerEntity playerEntity = item.getOwner() != null ? world.getPlayerByUuid(item.getOwner().getUuid()) : null;

        if (playerEntity instanceof ServerPlayerEntity) {
            PlayerDropCallback.EVENT.invoker().onDrop((ServerPlayerEntity) playerEntity, false, item.getStack());
        }
    }
}
