package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

import com.enthusiasm.plurelogger.listener.events.EntityKillEvent;
import com.enthusiasm.plurelogger.listener.events.EntityModifyEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {
    @Unique
    private NbtCompound oldEntityTags;
    @Unique
    private ItemStack oldEntityStack;

    @Inject(method = "equip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"))
    private void logOldEntity(PlayerEntity player, EquipmentSlot slot, ItemStack playerStack, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        this.oldEntityTags = entity.writeNbt(new NbtCompound());
        this.oldEntityStack = entity.getEquippedStack(slot);
    }

    @Inject(method = "equip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void armorStandInteract(PlayerEntity player, EquipmentSlot slot, ItemStack playerStack, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EntityModifyEvent event;

        if (!oldEntityStack.isEmpty()) {
            EntityModifyEvent.invoke(player.getWorld(), entity.getBlockPos(), oldEntityTags, entity, oldEntityStack, player, Sources.REMOVE.getSource());
        }

        if (!playerStack.isEmpty()) {
            EntityModifyEvent.invoke(player.getWorld(), entity.getBlockPos(), oldEntityTags, entity, playerStack, player, Sources.EQUIP.getSource());
        }
    }

    @Inject(method = "updateHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;kill()V"))
    private void armorStandKill(DamageSource damageSource, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EntityKillEvent.invoke(entity.getWorld(), entity.getBlockPos(), entity, damageSource);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;kill()V"))
    private void armorStandKill(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        EntityKillEvent.invoke(entity.getWorld(), entity.getBlockPos(), entity, source);
    }
}
