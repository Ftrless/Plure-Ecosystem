package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockMeltEvent;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin {
    @Inject(method = "melt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public void logFrostedIceDecayAir(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        BlockMeltEvent.invoke(world, pos, state, Blocks.AIR.getDefaultState(), world.getBlockEntity(pos));
    }

    @Inject(method = "melt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", shift = At.Shift.AFTER))
    public void logFrostedIceDecayWater(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        BlockMeltEvent.invoke(world, pos, state, world.getBlockState(pos), world.getBlockEntity(pos));
    }
}
