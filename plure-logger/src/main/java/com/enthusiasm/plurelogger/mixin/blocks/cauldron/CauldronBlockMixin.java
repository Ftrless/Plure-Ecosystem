package com.enthusiasm.plurelogger.mixin.blocks.cauldron;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(CauldronBlock.class)
public abstract class CauldronBlockMixin {
    @Inject(method = "fillFromDripstone", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", shift = At.Shift.AFTER))
    private void logIncrementLevelCauldron(BlockState state, World world, BlockPos pos, Fluid fluid, CallbackInfo ci) {
        BlockChangeEvent.invoke(
                world,
                pos,
                state,
                world.getBlockState(pos),
                null,
                null,
                Sources.DRIP.getSource()
        );
    }

    @Inject(method = "precipitationTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", shift = At.Shift.AFTER))
    private void logIncrementLevelCauldron(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci) {
        BlockChangeEvent.invoke(
                world,
                pos,
                state,
                world.getBlockState(pos),
                null,
                null,
                Sources.SNOW.getSource()
        );
    }
}
