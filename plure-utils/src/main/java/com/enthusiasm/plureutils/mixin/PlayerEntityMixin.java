package com.enthusiasm.plureutils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plureutils.PermissionsHolder;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    public void onDeath(CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);

        if (PermissionsHolder.check(player, PermissionsHolder.Permission.KEEP_INV, 4)) {
            ci.cancel();
        }
    }

    @Inject(method = "getXpToDrop", at = @At("HEAD"), cancellable = true)
    public void onGetXpToDrop(CallbackInfoReturnable<Integer> ci) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);

        if (PermissionsHolder.check(player, PermissionsHolder.Permission.KEEP_INV, 4) || player.isSpectator()) {
            ci.setReturnValue(0);
        }
    }
}
