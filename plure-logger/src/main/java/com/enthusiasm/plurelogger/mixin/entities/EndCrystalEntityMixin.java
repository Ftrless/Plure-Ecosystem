package com.enthusiasm.plurelogger.mixin.entities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

import com.enthusiasm.plurelogger.actionutils.IPlayerCausable;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalEntityMixin implements IPlayerCausable {

    @Unique
    private PlayerEntity causingPlayer;

    @Unique
    @Override
    public PlayerEntity getCausablePlayer() {
        return causingPlayer;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"))
    public void correctEndCrystalEntitySource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getSource() instanceof PlayerEntity player) {
            this.causingPlayer = player;
        } else if (source.getSource() instanceof ProjectileEntity projectile && projectile.getOwner() instanceof PlayerEntity player) {
            this.causingPlayer = player;
        }
    }
}
