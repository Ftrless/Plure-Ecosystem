package com.enthusiasm.plurelogger.mixin.blocks.lectern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurelogger.listener.events.ItemRemoveEvent;
import com.enthusiasm.plurelogger.utils.PlayerLecternHolder;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(LecternScreenHandler.class)
public class LecternScreenHandlerMixin {
    @Inject(method = "onButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;markDirty()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void logPickBook(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir, ItemStack itemStack) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        BlockEntity blockEntity = PlayerLecternHolder.getActiveHolders().get(player);
        ItemRemoveEvent.invoke(itemStack, blockEntity.getPos(), serverPlayer.getServerWorld(), Sources.PLAYER.getSource(), serverPlayer);
    }
}
