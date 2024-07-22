package com.enthusiasm.plureutils.service;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.WeatherManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class VoteService {
    private static final HashMap<String, HashMap<String, Boolean>> voteRecords = new HashMap<>();

    public static void startVote(String voteType, MinecraftServer server) {
        voteRecords.put(voteType, new HashMap<>());

        ThreadUtils.schedule(() -> endVote(voteType, server), 15000);

        MutableText resultMessage = voteMessage(voteType.equals("voteday") ? "/vote day" : "/vote sun", voteType);
        server.getPlayerManager().broadcast(resultMessage, false);
    }

    public static boolean checkVote(String voteType) {
        return voteRecords.containsKey(voteType);
    }

    public static boolean checkPlayerVote(String playerName, String voteType) {
        return voteRecords.get(voteType).containsKey(playerName);
    }

    public static void vote(String playerName, String voteType, boolean vote) {
        if (checkVote(voteType)) {
            voteRecords.get(voteType).put(playerName, vote);
        }
    }

    private static void endVote(String voteType, MinecraftServer server) {
        if (voteRecords.containsKey(voteType)) {
            Map<String, Boolean> votes = voteRecords.get(voteType);

            ServerWorld world = server.getOverworld();

            int pros = (int) votes.values().stream().filter(Boolean::booleanValue).count();
            int cons = votes.size() - pros;

            MutableText resultMessage =
                    TextUtils.translation("event.vote.end.feedback",
                            FormatUtils.Colors.DEFAULT,
                            Text.translatable(
                                    voteType.equals("voteday")
                                            ? "translate.voteday"
                                            : "translate.votesun"
                            ).setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)),
                            Text.literal(pros + "✔").setStyle(Style.EMPTY.withColor(Formatting.GREEN)),
                            Text.literal(cons + "✖").setStyle(Style.EMPTY.withColor(Formatting.RED))
                    );

            if (pros > cons) {
                switch (voteType) {
                    case "voteday" -> WeatherManager.setDay(world);
                    case "votesun" -> WeatherManager.setSunny(world);
                }
            }

            server.getPlayerManager().broadcast(resultMessage, false);

            voteRecords.remove(voteType);
        }
    }

    private static MutableText voteMessage(String command, String voteType) {
        MutableText voteYes = Text.literal(" [✔]").setStyle(
                        voteStyle(Formatting.GREEN, command + " true", TextUtils.translation("cmd.vote.hover.yes", FormatUtils.Colors.DEFAULT))
                );

        MutableText voteNo = Text.literal(" [✖]").setStyle(
                        voteStyle(Formatting.RED, command + " false", TextUtils.translation("cmd.vote.hover.no", FormatUtils.Colors.DEFAULT))
                );

        return TextUtils.translation("event.vote.start.feedback",
                        FormatUtils.Colors.DEFAULT,
                        Text.translatable(
                                voteType.equals("voteday")
                                    ? "translate.voteday"
                                    : "translate.votesun"
                        ).setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE))
                ).append(voteYes).append(voteNo);
    }

    private static Style voteStyle(Formatting color, String command, MutableText hoverMessage) {
        return Style.EMPTY
                .withColor(color)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
    }
}
