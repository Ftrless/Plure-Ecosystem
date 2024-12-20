package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RootedDirtBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(RootedDirtBlock.class)
public abstract class RootedDirtBlockMixin {
    @Inject(method = "grow", at = @At("HEAD"))
    public void logHangingRootsGrowth(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        BlockPlaceEvent.invoke(world, pos.down(), Blocks.HANGING_ROOTS.getDefaultState(), null, Sources.GROW.getSource());
    }
}
