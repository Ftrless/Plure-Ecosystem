package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ScaffoldingBlock.class)
public abstract class ScaffoldingBlockMixin {
    @Inject(method = "scheduledTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;breakBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public void logScaffoldLostBottomBreak(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockBreakEvent.invoke(world, pos, state, null, Sources.GRAVITY.getSource());
    }
}
