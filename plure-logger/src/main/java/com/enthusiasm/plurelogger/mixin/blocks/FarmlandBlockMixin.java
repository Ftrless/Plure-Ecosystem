package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {
    @Inject(method = "setToDirt", at = @At("HEAD"))
    private static void logSetToDirt(Entity entity, BlockState blockState, World world, BlockPos pos, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            BlockChangeEvent.invoke(world, pos, blockState, Blocks.DIRT.getDefaultState(), null, null, Sources.TRAMPLE.getSource(), player);
        } else {
            BlockChangeEvent.invoke(world, pos, blockState, Blocks.DIRT.getDefaultState(), null, null, Sources.TRAMPLE.getSource());
        }
    }
}
