package com.enthusiasm.plureeconomy.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureeconomy.api.EconomyActions;
import com.enthusiasm.plureeconomy.api.EconomyAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public class MoneyTake implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);
        double amount = DoubleArgumentType.getDouble(context, "amount");

        exec(context, targetPlayer, amount);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity targetPlayer, double amount) throws CommandSyntaxException {
        EconomyAPI
                .getPlayerMoney(targetPlayer)
                .thenAcceptAsync(playerMoney -> {
                    MutableText playerNotFound = TextUtils.translation("cmd.money.error.player-not-found", FormatUtils.Colors.ERROR);
                    MutableText insufficientMoney = TextUtils.translation("cmd.money.error.insufficient-money", FormatUtils.Colors.ERROR);

                    if (playerMoney == null) {
                        PlayerUtils.sendFeedback(context, playerNotFound);
                        return;
                    }

                    if (playerMoney < amount) {
                        PlayerUtils.sendFeedback(context, insufficientMoney);
                        return;
                    }

                    EconomyAPI.updatePlayerMoney(targetPlayer, playerMoney, amount, EconomyActions.TAKE);
                    PlayerUtils.sendFeedback(
                            context,
                            "cmd.money.take.feedback",
                            targetPlayer.getEntityName(),
                            amount,
                            TextUtils.declensionWord((long) amount, EconomyAPI.DECLENSIONED_NAME)
                    );
                });
    }
}
