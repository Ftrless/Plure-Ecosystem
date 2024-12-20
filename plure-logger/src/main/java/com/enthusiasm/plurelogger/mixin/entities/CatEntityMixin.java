package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.enthusiasm.plurelogger.listener.events.EntityModifyEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin {
    @Unique
    private NbtCompound oldEntityTags;

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CatEntity;setCollarColor(Lnet/minecraft/util/DyeColor;)V"))
    private void logOldEntity(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        this.oldEntityTags = entity.writeNbt(new NbtCompound());
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CatEntity;setCollarColor(Lnet/minecraft/util/DyeColor;)V", shift = At.Shift.AFTER))
    private void catCollarColour(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EntityModifyEvent.invoke(player.getWorld(), entity.getBlockPos(), oldEntityTags, entity, player.getStackInHand(hand), player, Sources.DYE.getSource());
    }
}
