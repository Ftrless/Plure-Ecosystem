package com.enthusiasm.plurelogger.mixin;

import java.util.function.Predicate;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(FillCommand.class)
public abstract class FillCommandMixin {
    @Inject(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/argument/BlockStateArgument;setBlockState(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;I)Z"
            )
    )
    private static void logFillChanges(
            ServerCommandSource source,
            BlockBox range,
            BlockStateArgument block,
            @Coerce Object mode,
            Predicate<CachedBlockPosition> filter,
            CallbackInfoReturnable<Integer> cir,
            @Local BlockPos pos) {
        ServerWorld world = source.getWorld();
        BlockState oldState = world.getBlockState(pos);
        BlockEntity oldBlockEntity = world.getBlockEntity(pos);
        BlockState newState = block.getBlockState();
        Entity entity = source.getEntity();
        PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

        if (!oldState.isAir()) {
            BlockBreakEvent.invoke(world, pos.toImmutable(), oldState, oldBlockEntity, Sources.COMMAND.getSource(), player);
        }

        BlockPlaceEvent.invoke(world, pos.toImmutable(), newState, null, Sources.COMMAND.getSource(), player);
    }
}
