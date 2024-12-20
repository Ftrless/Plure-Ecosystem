package com.enthusiasm.plurelogger.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$9")
public abstract class BucketDispenserBehaviorMixin extends ItemDispenserBehavior {
    @Inject(
            method = "dispenseSilently",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/FluidDrainable;tryDrainFluid(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;",
                    shift = At.Shift.AFTER
            )
    )
    private void logFluidPickup(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir, @Local ItemStack itemStack, @Local BlockPos pos, @Local BlockState blockState) {
        var world = pointer.getWorld();

        if (!itemStack.isEmpty()) {
            if (blockState.isLiquid() || blockState.isOf(Blocks.POWDER_SNOW)) {
                BlockBreakEvent.invoke(world, pos, blockState, world.getBlockEntity(pos), Sources.REDSTONE.getSource());
            } else {
                BlockChangeEvent.invoke(
                        world,
                        pos,
                        blockState,
                        world.getBlockState(pos),
                        world.getBlockEntity(pos),
                        world.getBlockEntity(pos),
                        Sources.REDSTONE.getSource()
                );
            }
        }
    }

}
