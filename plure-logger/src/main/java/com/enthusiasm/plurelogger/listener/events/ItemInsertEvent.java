package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ItemInsertEvent {
    void onItemInsert(ItemStack stack, BlockPos pos, World world, String source, ServerPlayerEntity player);

    Event<ItemInsertEvent> EVENT = EventFactory.createArrayBacked(ItemInsertEvent.class,
            (listeners) -> (stack, pos, world, source, player) -> {
                for (ItemInsertEvent listener : listeners) {
                    listener.onItemInsert(stack, pos, world, source, player);
                }
            }
    );

    static void invoke(ItemStack stack, BlockPos pos, World world, String source, ServerPlayerEntity player) {
        EVENT.invoker().onItemInsert(stack, pos, world, source, player);
    }
}
