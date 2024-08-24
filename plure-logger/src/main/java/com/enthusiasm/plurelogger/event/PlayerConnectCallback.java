package com.enthusiasm.plurelogger.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerConnectCallback {
    Event<PlayerConnectCallback> EVENT = EventFactory.createArrayBacked(PlayerConnectCallback.class, (listeners) -> (player, connect) -> {
        for (PlayerConnectCallback event : listeners) {
            event.onConnect(player, connect);
        }
    });

    void onConnect(ServerPlayerEntity playerEntity, boolean connect);
}
