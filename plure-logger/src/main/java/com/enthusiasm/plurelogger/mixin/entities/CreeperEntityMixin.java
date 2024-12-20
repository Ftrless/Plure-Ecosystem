package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.enthusiasm.plurelogger.actionutils.IPlayerCausable;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin implements IPlayerCausable {
    @Unique
    private PlayerEntity ignitePlayer;

    @Unique
    @Override
    public PlayerEntity getCausablePlayer() {
        if (ignitePlayer != null) return ignitePlayer;
        if (((CreeperEntity) (Object) this).getTarget() instanceof PlayerEntity player) return player;
        return null;
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/CreeperEntity;ignite()V"))
    private void trackIgnitingPlayer(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ignitePlayer = player;
    }
}
