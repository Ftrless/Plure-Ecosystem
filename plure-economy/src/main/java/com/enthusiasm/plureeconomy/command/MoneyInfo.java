package com.enthusiasm.plureeconomy.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureeconomy.api.EconomyAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public class MoneyInfo implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayer();

        exec(context, senderPlayer, false);

        return SINGLE_SUCCESS;
    }

    public int runForTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);

        exec(context, targetPlayer, true);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity targetPlayer, boolean forTarget) throws CommandSyntaxException {
        EconomyAPI
                .getPlayerMoney(targetPlayer)
                .thenAcceptAsync(playerMoney -> {
                    if (!forTarget) {
                        PlayerUtils.sendFeedback(
                                context,
                                "cmd.money.info.feedback",
                                playerMoney,
                                TextUtils.declensionWord(playerMoney.longValue(), EconomyAPI.DECLENSIONED_NAME)
                        );
                        return;
                    }

                    MutableText playerNotFound = TextUtils.translation("cmd.money.error.player-not-found", FormatUtils.Colors.ERROR);

                    if (playerMoney == null) {
                        PlayerUtils.sendFeedback(context, playerNotFound);
                        return;
                    }

                    PlayerUtils.sendFeedback(
                            context,
                            "cmd.money.info.feedback.target",
                            targetPlayer.getEntityName(),
                            playerMoney,
                            TextUtils.declensionWord(playerMoney.longValue(), EconomyAPI.DECLENSIONED_NAME)
                    );
                });
    }
}
