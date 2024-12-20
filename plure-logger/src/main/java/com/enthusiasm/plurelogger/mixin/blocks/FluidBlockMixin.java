package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {
    @ModifyArgs(
            method = "receiveNeighborFluids",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z")
    )
    private void logFluidBlockForm(Args args, World world, BlockPos pos, BlockState state) {
        BlockPos blockPos = args.get(0);
        BlockState blockState = args.get(1);

        BlockPlaceEvent.invoke(world, blockPos, blockState, null, Sources.FLUID.getSource());
    }
}
