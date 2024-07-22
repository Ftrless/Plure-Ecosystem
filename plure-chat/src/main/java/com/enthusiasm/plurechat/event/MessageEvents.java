package com.enthusiasm.plurechat.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class MessageEvents {
    public static final Event<ChatMessage> CHAT_MESSAGE = EventFactory.createArrayBacked(ChatMessage.class, handlers -> (message, sender) -> {
        for (ChatMessage handler : handlers) {
            handler.onChatMessage(message, sender);
        }
    });

    public static final Event<PrivateChatMessage> PRIVATE_CHAT_MESSAGE = EventFactory.createArrayBacked(PrivateChatMessage.class, handlers -> (message, sender, target) -> {
        for (PrivateChatMessage handler : handlers) {
            handler.onPrivateChatMessage(message, sender, target);
        }
    });

    @FunctionalInterface
    public interface ChatMessage {
        void onChatMessage(String message, ServerPlayerEntity sender);
    }

    @FunctionalInterface
    public interface PrivateChatMessage {
        void onPrivateChatMessage(String message, ServerPlayerEntity sender, ServerPlayerEntity target);
    }
}
