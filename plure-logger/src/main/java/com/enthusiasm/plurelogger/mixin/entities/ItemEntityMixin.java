package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.enthusiasm.plurelogger.listener.events.ItemPickUpEvent;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Unique
    private ItemEntity itemEntity;

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"))
    private void storeEntity(PlayerEntity player, CallbackInfo ci) {
        itemEntity = ((ItemEntity) (Object) this).copy();
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V"))
    private void logPlayerItemPickUp(PlayerEntity player, CallbackInfo ci) {
        ItemPickUpEvent.invoke(itemEntity, player);
    }
}
