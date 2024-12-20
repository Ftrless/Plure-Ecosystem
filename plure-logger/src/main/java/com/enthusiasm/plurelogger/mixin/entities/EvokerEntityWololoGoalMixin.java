package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

import com.enthusiasm.plurelogger.listener.events.EntityModifyEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(EvokerEntity.WololoGoal.class)
public abstract class EvokerEntityWololoGoalMixin {
    @Unique
    private NbtCompound oldEntityTags;

    @Inject(method = "castSpell", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;setColor(Lnet/minecraft/util/DyeColor;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void logOldEntity(CallbackInfo ci, SheepEntity sheepEntity) {
        if (sheepEntity.getColor() != DyeColor.RED) {
            this.oldEntityTags = sheepEntity.writeNbt(new NbtCompound());
        }
    }

    @Inject(method = "castSpell", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;setColor(Lnet/minecraft/util/DyeColor;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void evokerDyeSheep(CallbackInfo ci, SheepEntity sheepEntity) {
        if (oldEntityTags != null) {
            EntityModifyEvent.invoke(sheepEntity.getWorld(), sheepEntity.getBlockPos(), oldEntityTags, sheepEntity, Items.RED_DYE.getDefaultStack(), null, Sources.DYE.getSource());
        }
    }
}
