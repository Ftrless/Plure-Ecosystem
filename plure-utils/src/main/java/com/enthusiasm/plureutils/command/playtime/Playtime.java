package com.enthusiasm.plureutils.command.playtime;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

public class Playtime implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        execForSinglePlayer(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public int runForTargetPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);

        if (targetPlayer == null) {
            throw CommandHelper.createException(TextUtils.translation("cmd.playtime.target.error.player-not-found", FormatUtils.Colors.ERROR));
        }

        execForTargetPlayer(context, targetPlayer);

        return SINGLE_SUCCESS;
    }

    private void execForSinglePlayer(CommandContext<ServerCommandSource> context,
                                     ServerPlayerEntity senderPlayer) {
        long playTime = senderPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
        String formattedTime = formatPlaytime(playTime);
        PlayerUtils.sendFeedback(context, "cmd.playtime.feedback", formattedTime);
    }

    private void execForTargetPlayer(CommandContext<ServerCommandSource> context, ServerPlayerEntity targetPlayer) {
        long playTime = targetPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
        String formattedTime = formatPlaytime(playTime);
        PlayerUtils.sendFeedback(context, "cmd.playtime.target.feedback", targetPlayer.getName(), formattedTime);
    }


    private String formatPlaytime(long playTime) {
        long days = playTime / (20 * 60 * 60 * 24);
        long hours = (playTime % (20 * 60 * 60 * 24)) / (20 * 60 * 60);
        long minutes = (playTime % (20 * 60 * 60)) / (20 * 60);

        return String.format("%dд. %dч. %dм.", days, hours, minutes);
    }
}
