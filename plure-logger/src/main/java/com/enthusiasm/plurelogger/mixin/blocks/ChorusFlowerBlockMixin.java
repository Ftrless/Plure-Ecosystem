package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ChorusFlowerBlock.class)
public abstract class ChorusFlowerBlockMixin {
    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"))
    public void logChorusFlowerBreak(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile, CallbackInfo ci) {
        Entity entity = projectile.getOwner();

        if (entity instanceof PlayerEntity player) {
            BlockBreakEvent.invoke(world, hit.getBlockPos(), state, null, Sources.PROJECTILE.getSource(), player);
        } else {
           BlockBreakEvent.invoke(world, hit.getBlockPos(), state, null, Sources.PROJECTILE.getSource());
        }
    }
}
