package com.enthusiasm.plurelogger.mixin.entities;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;

import com.enthusiasm.plurelogger.actionutils.IPlayerCausable;

@Mixin(ExplosiveProjectileEntity.class)
public abstract class ExplosiveProjectileEntityMixin implements IPlayerCausable {
    @Nullable
    @Override
    public PlayerEntity getCausablePlayer() {
        if (((ExplosiveProjectileEntity) (Object) this).getOwner() instanceof MobEntity entity) {
            if (entity.getTarget() instanceof PlayerEntity player) {
                return player;
            }
        }
        return null;
    }
}
