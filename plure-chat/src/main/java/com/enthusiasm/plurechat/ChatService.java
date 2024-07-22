package com.enthusiasm.plurechat;

import com.enthusiasm.plurechat.api.MessageEventsAPI;
import com.enthusiasm.plurechat.compat.LuckPermsAPI;
import com.enthusiasm.plurechat.event.MessageEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.stream.Stream;

public class ChatService {
    private static final Text INDENT = Text.of(" ");
    private static final Style DISPLAY_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);

    public static void init() {
        ServerMessageEvents.ALLOW_GAME_MESSAGE.register((server, message, overlay) -> false);

        MessageEvents.CHAT_MESSAGE.register(ChatService::onChatMessage);
        MessageEvents.PRIVATE_CHAT_MESSAGE.register(ChatService::onPrivateChatMessage);
    }

    private static void onChatMessage(String message, ServerPlayerEntity sender) {
        MutableText messageBuilder = Text.empty();

        messageBuilder.append(getDisplayType(message, false, false));
        messageBuilder.append(INDENT);
        messageBuilder.append(getDisplayPrefix(sender));
        messageBuilder.append(sender.getDisplayName());
        messageBuilder.append(INDENT);
        messageBuilder.append(wrapContentWithStyle("»", DISPLAY_STYLE));
        messageBuilder.append(INDENT);
        messageBuilder.append(message.replace("!", ""));

        MessageEventsAPI.CHAT_MESSAGE.invoker().onChatMessage(messageBuilder);
        PlureChatEntrypoint.LOGGER.info(messageBuilder.getString());

        sendMessageByType(sender, messageBuilder, message.startsWith("!"));
    }

    private static void onPrivateChatMessage(String message, ServerPlayerEntity sender, ServerPlayerEntity target) {
        MutableText messageBuilder = Text.empty();

        messageBuilder.append(wrapContentWithStyle("[", DISPLAY_STYLE));
        messageBuilder.append(wrapContentWithStyle("PM", Style.EMPTY.withColor(rgbToMinecraftColor(100,149,237))));
        messageBuilder.append(wrapContentWithStyle("]", DISPLAY_STYLE));
        messageBuilder.append(INDENT);

        MutableText messageBuilderForSender = messageBuilder.copy();
        MutableText messageBuilderForTarget = messageBuilder.copy();

        messageBuilderForSender.append(getDisplayType(message, true, true));
        messageBuilderForSender.append(INDENT);
        messageBuilderForSender.append(wrapContentWithStyle(target.getEntityName(), Style.EMPTY.withColor(Formatting.WHITE)));
        messageBuilderForSender.append(INDENT);
        messageBuilderForSender.append(wrapContentWithStyle("»", DISPLAY_STYLE));
        messageBuilderForSender.append(INDENT);
        messageBuilderForSender.append(wrapContentWithStyle(message, Style.EMPTY.withColor(Formatting.WHITE)));

        messageBuilderForTarget.append(wrapContentWithStyle(sender.getEntityName(), Style.EMPTY.withColor(Formatting.WHITE)));
        messageBuilderForTarget.append(INDENT);
        messageBuilderForTarget.append(getDisplayType(message, true, false));
        messageBuilderForTarget.append(INDENT);
        messageBuilderForTarget.append(wrapContentWithStyle("»", DISPLAY_STYLE));
        messageBuilderForTarget.append(INDENT);
        messageBuilderForTarget.append(wrapContentWithStyle(message, Style.EMPTY.withColor(Formatting.WHITE)));

        messageBuilder.append(sender.getEntityName());
        messageBuilder.append(INDENT);
        messageBuilder.append(wrapContentWithStyle("→", DISPLAY_STYLE));
        messageBuilder.append(INDENT);
        messageBuilder.append(target.getEntityName());
        messageBuilder.append(INDENT);
        messageBuilder.append(wrapContentWithStyle("»", DISPLAY_STYLE));
        messageBuilder.append(INDENT);
        messageBuilder.append(message);

        MessageEventsAPI.PRIVATE_CHAT_MESSAGE.invoker().onPrivateChatMessage(messageBuilder);

        PlureChatEntrypoint.LOGGER.info(messageBuilder.getString());

        sender.sendMessage(messageBuilderForSender, false);
        target.sendMessage(messageBuilderForTarget, false);
    }

    private static MutableText getDisplayType(String message, boolean isPrivate, boolean forSender) {
        MutableText messageBuilder = Text.empty();

        if (!isPrivate) {
            boolean messageType = message.startsWith("!");

            messageBuilder.append(wrapContentWithStyle("[", DISPLAY_STYLE));
            messageBuilder.append(messageType ? "G" : "L").setStyle(Style.EMPTY.withColor(messageType ? Formatting.GOLD : Formatting.BLUE));
            messageBuilder.append(wrapContentWithStyle("]", DISPLAY_STYLE));

            return messageBuilder;
        }

        if (!forSender) {
            messageBuilder.append(wrapContentWithStyle("→", DISPLAY_STYLE));
            messageBuilder.append(INDENT);
            messageBuilder.append(wrapContentWithStyle("Я", Style.EMPTY.withColor(Formatting.AQUA)));
        } else {
            messageBuilder.append(wrapContentWithStyle("Я", Style.EMPTY.withColor(Formatting.AQUA)));
            messageBuilder.append(INDENT);
            messageBuilder.append(wrapContentWithStyle("→", DISPLAY_STYLE));
        }

        return messageBuilder;
    }

    private static MutableText getDisplayPrefix(ServerPlayerEntity player) {
        MutableText messageBuilder = Text.empty();

        if (!LuckPermsAPI.LOADED) return messageBuilder;

        String prefix = LuckPermsAPI.getPrefix(player);
        if (prefix == null || prefix.isEmpty()) return messageBuilder;

        messageBuilder.append(prefix);
        messageBuilder.append(INDENT);
        messageBuilder.append(wrapContentWithStyle("|", DISPLAY_STYLE));
        messageBuilder.append(INDENT);

        return messageBuilder;
    }

    private static void sendMessageByType(ServerPlayerEntity sender, MutableText text, boolean isGlobal) {
        MinecraftServer server = sender.server;
        Stream<ServerPlayerEntity> playerListStream = server.getPlayerManager().getPlayerList().parallelStream();

        if (!isGlobal) {
            playerListStream = playerListStream.filter(player -> player.squaredDistanceTo(sender) <= 200
                    && player.getEntityWorld().getRegistryKey().getValue() == sender.getEntityWorld().getRegistryKey().getValue()
            );
        }

        playerListStream.forEach(player -> player.sendMessage(text, false));
    }

    private static MutableText wrapContentWithStyle(String text, Style style) {
        return Text.of(text).copy().setStyle(style);
    }

    private static int rgbToMinecraftColor(int red, int green, int blue) {
        int r = MathHelper.clamp(red, 0, 255);
        int g = MathHelper.clamp(green, 0, 255);
        int b = MathHelper.clamp(blue, 0, 255);
        return (r << 16) + (g << 8) + b;
    }
}
