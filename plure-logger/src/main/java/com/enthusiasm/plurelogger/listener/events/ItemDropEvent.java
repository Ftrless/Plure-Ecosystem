package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface ItemDropEvent {
    void onItemDrop(ItemEntity entity, PlayerEntity player);

    Event<ItemDropEvent> EVENT = EventFactory.createArrayBacked(ItemDropEvent.class,
            (listeners) -> (entity, player) -> {
                for (ItemDropEvent listener : listeners) {
                    listener.onItemDrop(entity, player);
                }
            }
    );

    static void invoke(ItemEntity entity, PlayerEntity player) {
        EVENT.invoker().onItemDrop(entity, player);
    }
}
