package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(AbstractPlantStemBlock.class)
public abstract class AbstractPlantStemBlockMixin {
    @ModifyArgs(method = "grow", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    public void logPlantGrowth(Args args, ServerWorld world, Random random, BlockPos sourcePos, BlockState sourceState) {
        BlockPos pos = args.get(0);
        BlockState state = args.get(1);
        BlockPlaceEvent.invoke(world, pos, state, null, Sources.GROW.getSource());
    }
}
