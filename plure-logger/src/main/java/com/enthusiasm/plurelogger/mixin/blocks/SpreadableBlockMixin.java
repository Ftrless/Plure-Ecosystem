package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(SpreadableBlock.class)
public abstract class SpreadableBlockMixin {
    @Inject(method = "randomTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
            shift = At.Shift.AFTER))
    private void logGrassToDirt(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state == world.getBlockState(pos)) {
            return;
        }

        BlockChangeEvent.invoke(
                world,
                pos,
                state,
                world.getBlockState(pos),
                null,
                null,
                Sources.TRAMPLE.getSource()
        );
    }
}
