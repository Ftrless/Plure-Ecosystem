package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface ItemPickUpEvent {
    void onItemPickUp(ItemEntity entity, PlayerEntity player);

    Event<ItemPickUpEvent> EVENT = EventFactory.createArrayBacked(ItemPickUpEvent.class,
            (listeners) -> (entity, player) -> {
                for (ItemPickUpEvent listener : listeners) {
                    listener.onItemPickUp(entity, player);
                }
            }
    );

    static void invoke(ItemEntity entity, PlayerEntity player) {
        EVENT.invoker().onItemPickUp(entity, player);
    }
}
