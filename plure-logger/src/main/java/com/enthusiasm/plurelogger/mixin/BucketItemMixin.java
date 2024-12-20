package com.enthusiasm.plurelogger.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Shadow
    @Final
    private Fluid fluid;

    @Inject(method = "placeFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private void logFluidBreak(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        var blockstate = world.getBlockState(pos);
        if (!blockstate.isAir()) {
            BlockBreakEvent.invoke(world, pos, world.getBlockState(pos), world.getBlockEntity(pos), Sources.FLUID.getSource(), player);
        }
    }

    @Inject(method = "placeFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BucketItem;playEmptyingSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V", ordinal = 1))
    private void logFluidPlace(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        BlockPlaceEvent event;

        try {
            if (player != null) {
                BlockPlaceEvent.invoke(world, pos, fluid.getDefaultState().getBlockState(), null, player);
            } else {
                BlockPlaceEvent.invoke(world, pos, fluid.getDefaultState().getBlockState(), null, Sources.REDSTONE.getSource());
            }
        } catch (NullPointerException ignored) {}
    }

    @Inject(method = "placeFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BucketItem;playEmptyingSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V", ordinal = 0))
    private void logWaterlog(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, @Local BlockState blockState) {
        BlockChangeEvent event;

        if (player != null) {
            BlockChangeEvent.invoke(
                    world,
                    pos,
                    blockState,
                    world.getBlockState(pos),
                    world.getBlockEntity(pos),
                    world.getBlockEntity(pos),
                    player
            );
        } else {
            BlockChangeEvent.invoke(
                    world,
                    pos,
                    blockState,
                    world.getBlockState(pos),
                    world.getBlockEntity(pos),
                    world.getBlockEntity(pos),
                    Sources.REDSTONE.getSource()
            );
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"))
    private void logFluidPickup(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, @Local(ordinal = 0) BlockPos pos, @Local BlockState blockState) {
        if (blockState.getBlock() instanceof Waterloggable) {
            BlockChangeEvent.invoke(
                    world,
                    pos,
                    blockState,
                    world.getBlockState(pos),
                    world.getBlockEntity(pos),
                    world.getBlockEntity(pos),
                    player
            );
        } else {
            BlockBreakEvent.invoke(world, pos, blockState, world.getBlockEntity(pos), player);
        }
    }
}
