package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.utils.Sources;

public interface BlockBreakEvent {
    void onBlockBreak(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, String source, PlayerEntity player);

    Event<BlockBreakEvent> EVENT = EventFactory.createArrayBacked(BlockBreakEvent.class,
            (listeners) -> (world, pos, state, blockEntity, source, player) -> {
                for (BlockBreakEvent listener : listeners) {
                    listener.onBlockBreak(world, pos, state, blockEntity, source, player);
                }
            }
    );

    static void invoke(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, String source, PlayerEntity player) {
        EVENT.invoker().onBlockBreak(world, pos, state, blockEntity, source, player);
    }

    static void invoke(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, PlayerEntity player) {
        invoke(world, pos, state, blockEntity, Sources.PLAYER.getSource(), player);
    }

    static void invoke(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, String source) {
        invoke(world, pos, state, blockEntity, source, null);
    }
}
