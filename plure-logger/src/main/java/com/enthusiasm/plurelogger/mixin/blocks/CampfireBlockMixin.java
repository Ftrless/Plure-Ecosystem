package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {
    @Shadow
    @Final
    public static BooleanProperty LIT;

    @Inject(method = "onUse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void logCampfireAddItem(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir, BlockEntity oldBlockEntity) {
        BlockChangeEvent.invoke(world, pos, blockState, world.getBlockState(pos), oldBlockEntity, world.getBlockEntity(pos), Sources.INSERT.getSource(), player);
    }

    @Inject(method = "extinguish", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldAccess;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/BlockPos;)V"))
    private static void logCampfireExtinguish(Entity entity, WorldAccess worldAccess, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if (worldAccess instanceof World world) {
            if (entity instanceof PlayerEntity player) {
                BlockChangeEvent.invoke(world, pos, blockState, blockState.with(LIT, Boolean.FALSE), world.getBlockEntity(pos), null, Sources.EXTINGUISH.getSource(), player);
            } else {
                BlockChangeEvent.invoke(world, pos, blockState, blockState.with(LIT, Boolean.FALSE), world.getBlockEntity(pos), null, Sources.EXTINGUISH.getSource());
            }
        }
    }

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private void logCampfireProjectileIgnite(World world, BlockState blockState, BlockHitResult hit, ProjectileEntity projectile, CallbackInfo ci) {
        BlockChangeEvent.invoke(world, hit.getBlockPos(), blockState, blockState.with(LIT, Boolean.TRUE), world.getBlockEntity(hit.getBlockPos()), null, Sources.FIRE.getSource());
    }
}