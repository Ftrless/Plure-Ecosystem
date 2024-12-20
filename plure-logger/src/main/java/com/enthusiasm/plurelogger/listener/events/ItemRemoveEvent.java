package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ItemRemoveEvent {
    void onItemRemove(ItemStack stack, BlockPos pos, World world, String source, ServerPlayerEntity player);

    Event<ItemRemoveEvent> EVENT = EventFactory.createArrayBacked(ItemRemoveEvent.class,
            (listeners) -> (stack, pos, world, source, player) -> {
                for (ItemRemoveEvent listener : listeners) {
                    listener.onItemRemove(stack, pos, world, source, player);
                }
            }
    );

    static void invoke(ItemStack stack, BlockPos pos, World world, String source, ServerPlayerEntity player) {
        EVENT.invoker().onItemRemove(stack, pos, world, source, player);
    }
}
