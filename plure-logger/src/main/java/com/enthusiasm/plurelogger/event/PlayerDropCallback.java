package com.enthusiasm.plurelogger.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerDropCallback {
    Event<PlayerDropCallback> EVENT = EventFactory.createArrayBacked(PlayerDropCallback.class, (listeners) -> (playerEntity, drop, itemStack) -> {
        for (PlayerDropCallback event : listeners) {
            event.onDrop(playerEntity, drop, itemStack);
        }
    });

    void onDrop(ServerPlayerEntity playerEntity, boolean drop, ItemStack itemStack);
}
