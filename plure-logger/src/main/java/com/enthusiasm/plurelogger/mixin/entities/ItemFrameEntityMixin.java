package com.enthusiasm.plurelogger.mixin.entities;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.enthusiasm.plurelogger.listener.events.EntityKillEvent;
import com.enthusiasm.plurelogger.listener.events.EntityModifyEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin {
    @Shadow
    public abstract ItemStack getHeldItemStack();

    @Unique
    private NbtCompound oldEntityTags;

    @Inject(method = "interact", at = @At(value = "HEAD"))
    private void logOldEntity(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Entity entity = (Entity) (Object) this;
        oldEntityTags = entity.writeNbt(new NbtCompound());
    }

    @Inject(method = "dropHeldStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;)V"))
    private void logOldEntity2(@Nullable Entity entityActor, boolean alwaysDrop, CallbackInfo ci) {
        if (entityActor == null) {
            return;
        }
        Entity entity = (Entity) (Object) this;
        oldEntityTags = entity.writeNbt(new NbtCompound());
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void ledgerItemFrameEquip(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack playerStack = player.getStackInHand(hand);
        Entity entity = (Entity) (Object) this;
        EntityModifyEvent.invoke(player.getWorld(), entity.getBlockPos(), oldEntityTags, entity, playerStack, player, Sources.EQUIP.getSource());
    }

    @Inject(method = "dropHeldStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;)V"))
    private void itemFrameRemove(@Nullable Entity entityActor, boolean alwaysDrop, CallbackInfo ci) {
        ItemStack entityStack = this.getHeldItemStack();
        if (entityStack.isEmpty() || entityActor == null) {
            return;
        }
        Entity entity = (Entity) (Object) this;
        EntityModifyEvent.invoke(entityActor.getWorld(), entity.getBlockPos(), oldEntityTags, entity, entityStack, entityActor, Sources.REMOVE.getSource());
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setRotation(I)V", shift = At.Shift.AFTER))
    private void itemFrameRotate(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Entity entity = (Entity) (Object) this;
        EntityModifyEvent.invoke(player.getWorld(), entity.getBlockPos(), oldEntityTags, entity, null, player, Sources.ROTATE.getSource());
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/AbstractDecorationEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void itemFrameKill(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        EntityKillEvent.invoke(entity.getWorld(), entity.getBlockPos(), entity, source);
    }

    @Inject(method = "canStayAttached", at = @At(value = "RETURN"))
    private void itemFrameKill(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() == Boolean.FALSE) {
            Entity entity = (Entity) (Object) this;
            EntityKillEvent.invoke(entity.getWorld(), entity.getBlockPos(), entity, entity.getDamageSources().magic());
        }
    }
}
