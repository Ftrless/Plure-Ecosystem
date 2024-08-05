package com.enthusiasm.plureutils.command.tpa;

import com.enthusiasm.plurecore.utils.CommandUtils;
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

public class TpaDeny implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity initiator = EntityArgumentType.getPlayer(context, "target_player");
        ServerPlayerEntity receiver = context.getSource().getPlayer();

        exec(context, initiator, receiver);
        return SINGLE_SUCCESS;
    }

    public int runAuto(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity initiator = context.getSource().getPlayer();

        MutableText noActiveRequest = TextUtils.translation("cmd.tpadeny.error.no-active-request", FormatUtils.Colors.ERROR);
        List<TpaRequestEntry> candidates = TpaService.findReceiverRequests(initiator);

        if (candidates.isEmpty()) {
            throw CommandUtils.createException(noActiveRequest);
        }

        exec(context, candidates.getFirst().teleportFrom, candidates.getFirst().teleportTo);
        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity initiator, ServerPlayerEntity receiver) throws CommandSyntaxException {
        Optional<TpaRequestEntry> anyTpaRequest = TpaService.findFirstRequest(initiator, receiver);

        MutableText noActiveRequest = TextUtils.translation("cmd.tpadeny.error.no-active-request", FormatUtils.Colors.ERROR);

        if (anyTpaRequest.isEmpty()) {
            throw CommandUtils.createException(noActiveRequest);
        }

        TpaRequestEntry tpaRequest = anyTpaRequest.get();

        MutableText receiverMessage = TextUtils.translation("cmd.tpadeny.feedback.reciever", FormatUtils.Colors.DEFAULT);
        MutableText initiatorMessage = TextUtils.translation("cmd.tpadeny.feedback.initiator", FormatUtils.Colors.DEFAULT, receiver.getEntityName());

        tpaRequest.teleportTo.sendMessage(receiverMessage, false);
        tpaRequest.teleportFrom.sendMessage(initiatorMessage, false);
    }
}
