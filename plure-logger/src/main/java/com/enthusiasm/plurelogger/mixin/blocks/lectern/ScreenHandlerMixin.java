package com.enthusiasm.plurelogger.mixin.blocks.lectern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.ScreenHandler;

import com.enthusiasm.plurelogger.utils.PlayerLecternHolder;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(method = "onClosed", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;getCursorStack()Lnet/minecraft/item/ItemStack;"))
    public void onClosed(PlayerEntity player, CallbackInfo ci) {
        if (player.currentScreenHandler instanceof LecternScreenHandler) {
            PlayerLecternHolder.getActiveHolders().remove(player);
        }
    }
}
