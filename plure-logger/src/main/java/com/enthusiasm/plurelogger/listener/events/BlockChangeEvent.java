package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.utils.Sources;

public interface BlockChangeEvent {
    void onBlockChange(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity oldBlockEntity, BlockEntity newBlockEntity, String source, PlayerEntity player);

    Event<BlockChangeEvent> EVENT = EventFactory.createArrayBacked(BlockChangeEvent.class,
            (listeners) -> (world, pos, oldState, newState, oldBlockEntity, newBlockEntity, source, player) -> {
                for (BlockChangeEvent listener : listeners) {
                    listener.onBlockChange(world, pos, oldState, newState, oldBlockEntity, newBlockEntity, source, player);
                }
            }
    );

    static void invoke(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity oldBlockEntity, BlockEntity newBlockEntity, String source, PlayerEntity player) {
        EVENT.invoker().onBlockChange(world, pos, oldState, newState, oldBlockEntity, newBlockEntity, source, player);
    }

    static void invoke(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity oldBlockEntity, BlockEntity newBlockEntity, PlayerEntity player) {
        invoke(world, pos, oldState, newState, oldBlockEntity, newBlockEntity, Sources.PLAYER.getSource(), player);
    }

    static void invoke(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity oldBlockEntity, BlockEntity newBlockEntity, String source) {
        invoke(world, pos, oldState, newState, oldBlockEntity, newBlockEntity, source, null);
    }
}