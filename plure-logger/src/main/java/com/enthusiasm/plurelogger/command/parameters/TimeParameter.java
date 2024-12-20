package com.enthusiasm.plurelogger.command.parameters;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;

public class TimeParameter extends AbstractParameter<Instant> {
    private final List<Character> UNITS = List.of('s', 'm', 'h', 'd', 'w');
    private static final int MAX_SIZE = 9;

    @Override
    public Instant parse(StringReader reader) {
        int i = reader.getCursor();

        while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String input = reader.getString().substring(i, reader.getCursor()).toLowerCase();

        Pattern timePattern = Pattern.compile("([0-9]+)([smhdw])");
        Matcher matcher = timePattern.matcher(input);

        Duration duration = Duration.ZERO;
        while (matcher.find()) {
            long timeValue = Long.parseLong(matcher.group(1));
            String timeUnit = matcher.group(2);

            duration = switch (timeUnit) {
                case "s" -> duration.plusSeconds(timeValue);
                case "m" -> duration.plusMinutes(timeValue);
                case "h" -> duration.plusHours(timeValue);
                case "d" -> duration.plusDays(timeValue);
                case "w" -> duration.plusDays(timeValue * 7);
                default -> duration;
            };
        }

        return Instant.now().minus(duration);
    }

    private boolean isCharValid(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z');
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();

        for (Character unit : UNITS) {
            if (remaining.isEmpty()) {
                for (int i = 1; i <= MAX_SIZE; i++) {
                    builder.suggest(i + unit);
                }
            } else {
                char end = remaining.charAt(remaining.length() - 1);
                if (end >= '1' && end <= '9') {
                    builder.suggest(remaining + unit);
                }
            }
        }
        return builder.buildFuture();
    }
}
