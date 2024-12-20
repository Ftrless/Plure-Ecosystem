package com.enthusiasm.plurelogger.mixin.blocks.coral;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.block.CoralWallFanBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(CoralWallFanBlock.class)
public abstract class CoralWallFanBlockMixin {
    @ModifyArgs(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logCoralDeath(Args args, BlockState oldState, ServerWorld world, BlockPos pos, Random random) {
        BlockState newState = args.get(1);
        BlockChangeEvent.invoke(world, pos, oldState, newState, null, null, Sources.DECAY.getSource());
    }
}
