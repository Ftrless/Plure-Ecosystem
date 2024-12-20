package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.SpongeBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin {
    private BlockState oldBlockState;

    @Inject(method = "method_49829", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private static void logWaterDrainNonSource(BlockPos pos, World world, BlockPos currentPos, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakEvent.invoke(world, pos, world.getBlockState(pos), null, Sources.SPONGE.getSource());
    }

    @Inject(method = "method_49829", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/FluidDrainable;tryDrainFluid(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;"))
    private static void logWaterDrainSource(BlockPos pos, World world, BlockPos currentPos, CallbackInfoReturnable<Boolean> cir) {
        BlockBreakEvent.invoke(world, pos, world.getBlockState(pos), null, Sources.SPONGE.getSource());
    }

    @Inject(method = "update", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void storeState(World world, BlockPos pos, CallbackInfo ci) {
        oldBlockState = world.getBlockState(pos);
    }

    @Inject(method = "update", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logSpongeToWetSponge(World world, BlockPos pos, CallbackInfo ci) {
        BlockState newBlockState = world.getBlockState(pos);
        if (oldBlockState == newBlockState) {
            return;
        }
        BlockChangeEvent.invoke(world, pos, oldBlockState, newBlockState, null, null, Sources.WET.getSource());
    }
}
