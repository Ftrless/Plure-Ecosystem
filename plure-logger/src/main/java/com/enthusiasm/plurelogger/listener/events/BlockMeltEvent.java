package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockMeltEvent {
    void onBlockMelt(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity blockEntity);

    Event<BlockMeltEvent> EVENT = EventFactory.createArrayBacked(BlockMeltEvent.class,
            (listeners) -> (world, pos, oldState, newState, blockEntity) -> {
                for (BlockMeltEvent listener : listeners) {
                    listener.onBlockMelt(world, pos, oldState, newState, blockEntity);
                }
            }
    );

    static void invoke(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity blockEntity) {
        EVENT.invoker().onBlockMelt(world, pos, oldState, newState, blockEntity);
    }
}