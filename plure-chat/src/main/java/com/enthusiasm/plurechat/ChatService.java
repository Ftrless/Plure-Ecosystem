package com.enthusiasm.plurechat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.enthusiasm.plurechat.api.MessageEventsAPI;
import com.enthusiasm.plurechat.compat.LuckPermsAPI;
import com.enthusiasm.plurechat.data.DataManager;
import com.enthusiasm.plurechat.event.MessageEvents;
import com.enthusiasm.plurecore.utils.text.FormatUtils;

public class ChatService {
    private static final Text INDENT = Text.of(" ");
    private static final Style DISPLAY_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);
    private static final Pattern PATTERN = Pattern.compile(
            "&([0-9a-fk-or])|(((http?|https)://)?((W|w){3}.)?[a-zA-Z0-9]+\\.[a-zA-Z]+)"
    );

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
        messageBuilder.append(
                parseText(
                        message.startsWith("!")
                                ? message.replaceFirst("!", "")
                                : message
                )
        );

        MessageEventsAPI.CHAT_MESSAGE.invoker().onChatMessage(messageBuilder);
        PlureChatEntrypoint.LOGGER.info(messageBuilder.getString());

        sendMessageByType(sender, messageBuilder, message.startsWith("!"));
    }

    private static void onPrivateChatMessage(String message, ServerPlayerEntity sender, ServerPlayerEntity target) {
        MutableText messageBuilder = Text.empty();

        messageBuilder.append(wrapContentWithStyle("[", DISPLAY_STYLE));
        messageBuilder.append(wrapContentWithStyle("PM", Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS))));
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
            messageBuilder.append(wrapContentWithStyle("Я", Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS))));
        } else {
            messageBuilder.append(wrapContentWithStyle("Я", Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS))));
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
        Stream<ServerPlayerEntity> playerListStream = sender.server.getPlayerManager().getPlayerList().stream();

        if (!isGlobal) {
            playerListStream = playerListStream.filter(player -> player.squaredDistanceTo(sender) <= 400
                    && player.getEntityWorld().getRegistryKey().getValue() == sender.getEntityWorld().getRegistryKey().getValue()
                    && !DataManager.checkContainsIgnorableUser(player.getUuid(), sender.getUuid())
            );
        }

        playerListStream.forEach(player -> {
            if (!DataManager.checkContainsIgnorableUser(player.getUuid(), sender.getUuid())) {
                player.sendMessage(text, false);
            }
        });
    }

    private static MutableText wrapContentWithStyle(String text, Style style) {
        return Text.of(text).copy().setStyle(style);
    }

    private static MutableText parseText(String message) {
        MutableText messageBuilder = Text.empty();
        Matcher matcher = PATTERN.matcher(message);
        int lastEnd = 0;
        Style currentStyle = Style.EMPTY;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                messageBuilder.append(Text.literal(message.substring(lastEnd, matcher.start())).setStyle(currentStyle));
            }

            String url = matcher.group(2);
            if (url != null) {
                MutableText urlText = Text.literal(url)
                        .setStyle(currentStyle
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.startsWith("https") ? url : "https://" + url))
                        );
                messageBuilder.append(urlText);
            } else {
                char code = matcher.group(1).charAt(0);
                currentStyle = applyFormattingCode(currentStyle, code);
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < message.length()) {
            messageBuilder.append(Text.literal(message.substring(lastEnd)).setStyle(currentStyle));
        }

        return messageBuilder;
    }

    private static Style applyFormattingCode(Style style, char code) {
        return switch (code) {
            case '0' -> style.withColor(Formatting.BLACK);
            case '1' -> style.withColor(Formatting.DARK_BLUE);
            case '2' -> style.withColor(Formatting.DARK_GREEN);
            case '3' -> style.withColor(Formatting.DARK_AQUA);
            case '4' -> style.withColor(Formatting.DARK_RED);
            case '5' -> style.withColor(Formatting.DARK_PURPLE);
            case '6' -> style.withColor(Formatting.GOLD);
            case '7' -> style.withColor(Formatting.GRAY);
            case '8' -> style.withColor(Formatting.DARK_GRAY);
            case '9' -> style.withColor(Formatting.BLUE);
            case 'a' -> style.withColor(Formatting.GREEN);
            case 'b' -> style.withColor(Formatting.AQUA);
            case 'c' -> style.withColor(Formatting.RED);
            case 'd' -> style.withColor(Formatting.LIGHT_PURPLE);
            case 'e' -> style.withColor(Formatting.YELLOW);
            case 'f' -> style.withColor(Formatting.WHITE);
            case 'k' -> style.withObfuscated(true);
            case 'l' -> style.withBold(true);
            case 'm' -> style.withStrikethrough(true);
            case 'n' -> style.withUnderline(true);
            case 'o' -> style.withItalic(true);
            case 'r' -> Style.EMPTY;
            default -> style;
        };
    }
}
