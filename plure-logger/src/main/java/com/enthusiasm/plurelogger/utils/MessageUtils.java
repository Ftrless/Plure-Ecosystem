package com.enthusiasm.plurelogger.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.actionutils.SearchResults;
import com.enthusiasm.plurelogger.config.ConfigWrapper;

public class MessageUtils {
    public static void sendSearchResults(ServerCommandSource source, SearchResults results, Text header) {
        source.sendFeedback(() -> header, false);

        for (IActionType actionType : results.actions()) {
            source.sendFeedback(() -> actionType.getMessage(source), false);
        }

        source.sendFeedback(() -> Text.translatable("text.footer.search",
                Text.translatable("text.footer.page_backward").setStyle(TextColorPallet.getPrimaryVariant())
                        .styled(style -> results.page() > 1
                                ? style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.translatable("text.footer.page_backward.hover")))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/l pg " + (results.page() - 1)))
                                : Style.EMPTY),
                Text.literal(String.valueOf(results.page())).setStyle(TextColorPallet.getPrimaryVariant()),
                Text.literal(String.valueOf(results.pages())).setStyle(TextColorPallet.getPrimaryVariant()),
                Text.translatable("text.footer.page_forward").setStyle(TextColorPallet.getPrimaryVariant())
                        .styled(style -> results.page() < results.pages()
                                ? style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.translatable("text.footer.page_forward.hover")))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/l pg " + (results.page() + 1)))
                                : Style.EMPTY)).setStyle(TextColorPallet.getPrimary()), false);
    }

    public static void sendPlayerMessage(ServerCommandSource source, List<PlayerResult> results) {
        if (results.isEmpty()) {
            source.sendFeedback(() -> Text.translatable("error.command.no_results").setStyle(TextColorPallet.getPrimary()), false);
            return;
        }

        source.sendFeedback(() -> Text.translatable("text.header.search").setStyle(TextColorPallet.getSecondary()), false);

        for (PlayerResult result : results) {
            source.sendFeedback(result::toText, false);
        }
    }

    public static MutableText instantToText(Instant time) {
        Duration duration = Duration.between(time, Instant.now());
        MutableText text = Text.literal("");

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (days > 0) {
            text.append(String.valueOf(days)).append("d");
        } else if (hours > 0) {
            text.append(String.valueOf(hours)).append("h");
        } else if (minutes > 0) {
            text.append(String.valueOf(minutes)).append("m");
        } else {
            text.append(String.valueOf(seconds)).append("s");
        }

        MutableText message = Text.translatable("text.action_message.time_diff", text);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        MutableText timeMessage = Text.literal(formatter.format(time.atZone(ConfigWrapper.getConfig().timeZone)));

        message.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, timeMessage)));

        return message;
    }
}
