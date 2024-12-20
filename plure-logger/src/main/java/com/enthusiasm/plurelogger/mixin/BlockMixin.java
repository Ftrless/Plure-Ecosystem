package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(
            method = "replace(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;I)Z"))
    private static void tryLogSupportedBlockBreak(BlockState state, BlockState newState, WorldAccess worldAccess, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
        if (worldAccess instanceof World world) {
            BlockBreakEvent.invoke(
                    world,
                    pos.toImmutable(),
                    state,
                    state.hasBlockEntity()
                            ? world.getBlockEntity(pos)
                            : null,
                    Sources.GRAVITY.getSource()
            );
        }
    }
}
