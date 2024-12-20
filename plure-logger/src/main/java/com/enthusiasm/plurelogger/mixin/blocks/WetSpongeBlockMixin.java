package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.WetSpongeBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(WetSpongeBlock.class)
public abstract class WetSpongeBlockMixin {
    @Inject(method = "onBlockAdded", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logSpongeToWetSponge(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        BlockState newBlockState = world.getBlockState(pos);
        BlockChangeEvent.invoke(world, pos, state, newBlockState, null, null, Sources.DRY.getSource());
    }
}
