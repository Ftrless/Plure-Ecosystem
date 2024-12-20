package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin {
    @ModifyArgs(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logDaylightDetectorToggling(Args args, BlockState oldState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockState newState = args.get(1);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (player == null) {
            BlockChangeEvent.invoke(world, pos, oldState, newState, blockEntity, blockEntity, Sources.INTERACT.getSource());
        } else {
            BlockChangeEvent.invoke(world, pos, oldState, newState, blockEntity, blockEntity, player);
        }
    }
}
