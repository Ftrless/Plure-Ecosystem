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
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class TpaHere implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "target_player");

        exec(context, senderPlayer, targetPlayer);

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, ServerPlayerEntity targetPlayer) throws CommandSyntaxException {
        MutableText oneSelf = TextUtils.translation("cmd.tpahere.error.self-tpa", FormatUtils.Colors.ERROR);
        MutableText msgToDest = TextUtils.translation(
                "cmd.tpahere.feedback.receiver",
                FormatUtils.Colors.ERROR,
                senderPlayer.getEntityName(),
                voteYes,
                voteNo
        );

        if (senderPlayer.equals(targetPlayer)) {
            throw CommandUtils.createException(oneSelf);
        }

        TpaRequestEntry tpaRequest = new TpaRequestEntry(senderPlayer, targetPlayer, true);
        TpaService.addTpaRequest(tpaRequest);

        PlayerUtils.sendFeedback(context, "cmd.tpahere.feedback");
        targetPlayer.sendMessage(msgToDest, false);
    }

    static MutableText voteYes = Text.literal(" [✔]").setStyle(
            voteStyle(
                    Formatting.GREEN,
                    "/tpaaccept",
                    Text.empty()
                            .append(Text.literal("Принять запрос").formatted(Formatting.RED))
            )
    );

    static MutableText voteNo = Text.literal(" [✖]").setStyle(
            voteStyle(
                    Formatting.RED,
                    "/tpadeny",
                    Text.empty()
                            .append(Text.literal("Отклонить запрос").formatted(Formatting.RED))
            )
    );

    private static Style voteStyle(Formatting color, String command, MutableText hoverMessage) {
        return Style.EMPTY
                .withColor(color)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
    }
}
