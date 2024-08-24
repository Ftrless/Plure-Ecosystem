package com.enthusiasm.plurekits.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.util.Util;

import com.enthusiasm.plurecore.utils.CommandUtils;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.TimeUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plurekits.KitService;
import com.enthusiasm.plurekits.PermissionHolder;
import com.enthusiasm.plurekits.data.DataManager;
import com.enthusiasm.plurekits.data.kit.KitData;
import com.enthusiasm.plurekits.data.player.PlayerKitData;
import com.enthusiasm.plurekits.util.InventoryUtils;

public class KitClaim implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String kitName = StringArgumentType.getString(context, "kit_name");

        exec(
                context,
                context.getSource().getPlayer(),
                kitName
        );

        return SINGLE_SUCCESS;
    }

    public static void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, String kitName) throws CommandSyntaxException {
        PlayerKitData playerKitData = DataManager.getInstance().getPlayerKitData(senderPlayer);
        KitData kitData = KitService.getKit(kitName);

        MutableText notFound = TextUtils.translation("cmd.kit.claim.error.not-found", FormatUtils.Colors.ERROR, kitName);

        if (kitData == null) {
            throw CommandUtils.createException(notFound);
        }

        long currentTime = Util.getEpochTimeMs();
        long needsPlayedTime = (kitData.getNeedsPlayed() / 1000) / 3600;
        long remainingTime = (playerKitData.getKitUsedTime(kitName) + kitData.getCooldown()) - currentTime;
        long[] remainingPlayTime = getRemainingPlayTime(senderPlayer, kitData.getNeedsPlayed() / 1000);

        MutableText insufficientRights = TextUtils.translation("cmd.kit.claim.error.insufficient-rights", FormatUtils.Colors.ERROR, kitName);
        MutableText insufficientPlayingTime = TextUtils.translation(
                "cmd.kit.claim.error.insufficient-playing-time",
                FormatUtils.Colors.ERROR,
                kitName,
                needsPlayedTime,
                TextUtils.declensionWord(needsPlayedTime, TimeUtils.HOURS),
                remainingPlayTime[0],
                remainingPlayTime[1]
        );
        MutableText oneTimeUse = TextUtils.translation("cmd.kit.claim.error.one-time-use", FormatUtils.Colors.ERROR, kitName);
        MutableText onCooldown = TextUtils.translation("cmd.kit.claim.error.on-cooldown", FormatUtils.Colors.ERROR, kitName, TimeUtils.getFormattedRemainingTime(remainingTime));

        if (!PermissionHolder.check(senderPlayer, PermissionHolder.getKitPermission(kitName), 4)) {
            throw CommandUtils.createException(insufficientRights);
        }

        if (getPlayTime(senderPlayer) < needsPlayedTime) {
            throw CommandUtils.createException(insufficientPlayingTime);
        }

        if (kitData.getCooldown() < 0) {
            throw CommandUtils.createException(oneTimeUse);
        }

        if (remainingTime > 0) {
            throw CommandUtils.createException(onCooldown);
        }

        playerKitData.useKit(kitName);
        InventoryUtils.giveKit(senderPlayer, kitData);

        PlayerUtils.sendFeedback(context, "cmd.kit.claim.feedback", kitName);
    }

    public static long getPlayTime(ServerPlayerEntity senderPlayer) {
        long playTime = senderPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME)) / 20;

        return playTime / 3600;
    }

    public static long[] getRemainingPlayTime(ServerPlayerEntity senderPlayer, long time) {
        long playTimeInSeconds = senderPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME)) / 20;

        long remainingPlayTimeInSeconds = time - playTimeInSeconds;

        if (remainingPlayTimeInSeconds <= 0) {
            return new long[]{0, 0};
        }

        long remainingHours = remainingPlayTimeInSeconds / 3600;
        long remainingMinutes = (remainingPlayTimeInSeconds % 3600) / 60;

        return new long[]{remainingHours, remainingMinutes};
    }
}
