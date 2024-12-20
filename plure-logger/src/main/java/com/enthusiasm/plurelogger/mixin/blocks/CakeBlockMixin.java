package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    @Shadow
    @Final
    public static IntProperty BITES;

    @Inject(method = "tryEat", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private static void logCakeEat(
            WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        BlockChangeEvent.invoke(
                player.getWorld(),
                pos,
                world.getBlockState(pos),
                state.with(BITES, state.get(BITES) + 1),
                null,
                null,
                Sources.CONSUME.getSource(),
                player
        );
    }

    @Inject(method = "tryEat", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldAccess;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z",
            shift = At.Shift.AFTER))
    private static void logCakeEatAndRemove(
            WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        BlockChangeEvent.invoke(
                player.getWorld(),
                pos,
                state,
                world.getBlockState(pos),
                null,
                null,
                Sources.CONSUME.getSource(),
                player
        );
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
            shift = At.Shift.AFTER))
    private void logCakeAddCandle(
            BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        BlockChangeEvent.invoke(
                player.getWorld(),
                pos,
                state,
                world.getBlockState(pos),
                null,
                null,
                Sources.INTERACT.getSource(),
                player
        );
    }
}
