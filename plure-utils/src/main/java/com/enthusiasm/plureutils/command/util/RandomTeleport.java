package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.utils.CooldownUtils;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.config.ConfigManager;
import com.enthusiasm.plureutils.service.RandomTeleportService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class RandomTeleport implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        if (RandomTeleportService.isLocating(senderPlayer)) {
            return;
        }

        String formattedRemainingTime = CooldownUtils.getRemainingTime(CooldownUtils.getTargetCooldown("rtp", senderPlayer.getUuid()), ConfigManager.getConfig().rtpCooldown);
        Message onCooldown = TextUtils.translation("cooldown.target.feedback", FormatUtils.Colors.ERROR, formattedRemainingTime);

        if (!CooldownUtils.isTargetCooldownExpired("rtp", senderPlayer.getUuid(), ConfigManager.getConfig().rtpCooldown)) {
            throw CommandHelper.createException(onCooldown);
        }

        PlayerUtils.sendFeedback(context, "cmd.rtp.feedback.position-finding");

        RandomTeleportService teleportManager = new RandomTeleportService(
                senderPlayer.getServerWorld(),
                senderPlayer.getUuid()
        );

        teleportManager.findPosition((position) -> {
            if (position == null) {
                PlayerUtils.sendFeedback(context, "cmd.rtp.error.failed-to-find", FormatUtils.Colors.ERROR);
                return;
            }

            CooldownUtils.addTargetCooldown("rtp", senderPlayer.getUuid());

            PlayerUtils.sendFeedback(context, "cmd.rtp.feedback.position-found");

            ThreadUtils.schedule(() -> {
                PlayerUtils.teleportPlayer(
                        senderPlayer,
                        position.x,
                        position.y,
                        position.z,
                        senderPlayer.getYaw(),
                        senderPlayer.getPitch(),
                        senderPlayer.getServerWorld()
                );

                PlayerUtils.sendFeedback(context, "cmd.rtp.feedback",
                        String.format("%.0f", position.x),
                        String.format("%.0f", position.y),
                        String.format("%.0f", position.z)
                );
            }, 3000);
        });
    }
}
