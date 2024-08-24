package com.enthusiasm.plurechat.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public class MessageEventsAPI {
    public static final Event<ChatMessage> CHAT_MESSAGE = EventFactory.createArrayBacked(ChatMessage.class, handlers -> (message) -> {
        for (ChatMessage handler : handlers) {
            handler.onChatMessage(message);
        }
    });

    public static final Event<PrivateChatMessage> PRIVATE_CHAT_MESSAGE = EventFactory.createArrayBacked(PrivateChatMessage.class, handlers -> (message) -> {
        for (PrivateChatMessage handler : handlers) {
            handler.onPrivateChatMessage(message);
        }
    });

    public static final Event<CommandExecute> COMMAND_EXECUTE = EventFactory.createArrayBacked(CommandExecute.class, handlers -> (command, executor) -> {
        for (CommandExecute handler : handlers) {
            handler.onCommandExecute(command, executor);
        }
    });

    @FunctionalInterface
    public interface ChatMessage {
        void onChatMessage(MutableText message);
    }

    @FunctionalInterface
    public interface PrivateChatMessage {
        void onPrivateChatMessage(MutableText message);
    }

    @FunctionalInterface
    public interface CommandExecute {
        void onCommandExecute(String command, ServerPlayerEntity executor);
    }
}
