package com.enthusiasm.plureutils.command.tpa;

import com.enthusiasm.plurecore.utils.CommandUtils;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.service.tpa.TpaRequestEntry;
import com.enthusiasm.plureutils.service.tpa.TpaService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Optional;

public class TpaAccept implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity initiator = EntityArgumentType.getPlayer(context, "target_player");
        ServerPlayerEntity receiver = context.getSource().getPlayer();

        exec(context, initiator, receiver);
        return SINGLE_SUCCESS;
    }

    public int runAuto(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity receiver = context.getSource().getPlayer();

        MutableText noActiveRequest = TextUtils.translation("cmd.tpaaccept.error.no-active-request", FormatUtils.Colors.ERROR);
        List<TpaRequestEntry> candidates = TpaService.findReceiverRequests(receiver);

        if (candidates.isEmpty()) {
            throw CommandUtils.createException(noActiveRequest);
        }

        exec(context, candidates.getFirst().teleportFrom, candidates.getFirst().teleportTo);
        return SINGLE_SUCCESS;
    }

    public static void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity initiator, ServerPlayerEntity receiver) throws CommandSyntaxException {
        Optional<TpaRequestEntry> anyTpaRequest = TpaService.findFirstRequest(initiator, receiver);

        MutableText noActiveRequest = TextUtils.translation("cmd.tpaaccept.error.no-active-request", FormatUtils.Colors.ERROR);

        if (anyTpaRequest.isEmpty()) {
            throw CommandUtils.createException(noActiveRequest);
        }

        TpaRequestEntry tpaRequest = anyTpaRequest.get();
        PlayerUtils.teleportPlayer(
                tpaRequest.teleportFrom,
                tpaRequest.teleportTo.getX(),
                tpaRequest.teleportTo.getY(),
                tpaRequest.teleportTo.getZ(),
                tpaRequest.teleportTo.getYaw(),
                tpaRequest.teleportTo.getPitch(),
                tpaRequest.teleportTo.getServerWorld()
        );

        MutableText initiatorMessage = TextUtils.translation("cmd.tpaaccept.feedback.initiator", FormatUtils.Colors.DEFAULT, receiver.getEntityName());
        MutableText receiverMessage = TextUtils.translation("cmd.tpaaccept.feedback.receiver", FormatUtils.Colors.DEFAULT, initiator.getEntityName());
        MutableText initiatorMessageOneSelf = TextUtils.translation("cmd.tpaaccept.feedback.initiator.oneself", FormatUtils.Colors.DEFAULT, receiver.getEntityName());
        MutableText receiverMessageOneSelf = TextUtils.translation("cmd.tpaaccept.feedback.receiver.oneself", FormatUtils.Colors.DEFAULT, initiator.getEntityName());

        MutableText initiatorMessageTotal = tpaRequest.toOneSelf ? initiatorMessageOneSelf : initiatorMessage;
        MutableText receiverMessageTotal = tpaRequest.toOneSelf ? receiverMessageOneSelf : receiverMessage;

        tpaRequest.teleportFrom.sendMessage(initiatorMessageTotal, false);
        tpaRequest.teleportTo.sendMessage(receiverMessageTotal, false);

        TpaService.removeTpaRequest(tpaRequest, true);
    }
}
