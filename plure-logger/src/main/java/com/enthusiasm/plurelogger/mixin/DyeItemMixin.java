package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.enthusiasm.plurelogger.listener.events.EntityModifyEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin {
    @Unique
    private NbtCompound oldEntityTags;

    @Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;setColor(Lnet/minecraft/util/DyeColor;)V"))
    private void oldEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        oldEntityTags = entity.writeNbt(new NbtCompound());
    }

    @Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;setColor(Lnet/minecraft/util/DyeColor;)V", shift = At.Shift.AFTER))
    private void playerDyeSheep(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        EntityModifyEvent.invoke(player.getWorld(), entity.getBlockPos(), oldEntityTags, entity, stack, player, Sources.DYE.getSource());
    }
}
