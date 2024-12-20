package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(LilyPadBlock.class)
public abstract class LilyPadBlockMixin {
    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"))
    private void logLilyPadBreak(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        BoatEntity boat = (BoatEntity) entity;

        if (boat.getFirstPassenger() instanceof PlayerEntity player) {
            BlockBreakEvent.invoke(world, new BlockPos(pos), state, null, Sources.VEHICLE.getSource(), player);
        } else {
            BlockBreakEvent.invoke(world, new BlockPos(pos), state, null, Sources.VEHICLE.getSource());
        }
    }
}