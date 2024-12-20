package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {
    @Inject(method = "breakPatternBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void logStatueBreak(World world, BlockPattern.Result patternResult, CallbackInfo ci, int i, int j, CachedBlockPosition cachedBlockPosition) {
        if (cachedBlockPosition.getBlockState().isAir()) {
            return;
        }
        BlockBreakEvent.invoke(world, cachedBlockPosition.getBlockPos(), cachedBlockPosition.getBlockState(), cachedBlockPosition.getBlockEntity(), Sources.STATUE.getSource());
    }
}
