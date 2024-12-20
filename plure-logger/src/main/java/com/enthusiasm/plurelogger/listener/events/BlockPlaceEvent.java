package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.utils.Sources;

public interface BlockPlaceEvent {
    void onBlockPlace(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, String source, PlayerEntity player);

    Event<BlockPlaceEvent> EVENT = EventFactory.createArrayBacked(BlockPlaceEvent.class,
            (listeners) -> (world, pos, state, blockEntity, source, player) -> {
                for (BlockPlaceEvent listener : listeners) {
                    listener.onBlockPlace(world, pos, state, blockEntity, source, player);
                }
            }
    );

    static void invoke(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, String source, PlayerEntity player) {
        EVENT.invoker().onBlockPlace(world, pos, state, blockEntity, source, player);
    }

    static void invoke(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, PlayerEntity player) {
        invoke(world, pos, state, blockEntity, Sources.PLAYER.getSource(), player);
    }

    static void invoke(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, String source) {
        invoke(world, pos, state, blockEntity, source, null);
    }
}
