package com.enthusiasm.plureutils.mixin.vanish;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(value = {BarrelBlockEntity.class, ChestBlockEntity.class, EnderChestInventory.class, ShulkerBoxBlockEntity.class})
public abstract class ContainerMixin {
    @Inject(method = "onOpen", at = @At("HEAD"), cancellable = true)
    public void vanish_cancelOpenAnimation(PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer && VanishService.isVanished(serverPlayer)) {
            ci.cancel();
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    public void vanish_cancelCloseAnimation(PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer && VanishService.isVanished(serverPlayer)) {
            ci.cancel();
        }
    }
}
