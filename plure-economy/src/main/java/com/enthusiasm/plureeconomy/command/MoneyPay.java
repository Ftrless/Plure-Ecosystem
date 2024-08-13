package com.enthusiasm.plureeconomy.command;

import com.enthusiasm.plurecore.utils.CommandUtils;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureeconomy.api.EconomyWrapper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public class MoneyPay implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);
        double amount = DoubleArgumentType.getDouble(context, "amount");

        exec(context, senderPlayer, targetPlayer, amount);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity moneyFrom, ServerPlayerEntity moneyTo, double amount) throws CommandSyntaxException {
        EconomyWrapper
                .getPlayerMoney(moneyFrom)
                .thenAcceptAsync(playerMoney -> {
                    MutableText insufficientMoney = TextUtils.translation("cmd.money.error.insufficient-money", FormatUtils.Colors.ERROR);

                    if (playerMoney < amount) {
                        PlayerUtils.sendFeedback(context, insufficientMoney);
                        return;
                    };

                    EconomyWrapper.transferPlayerMoney(moneyFrom, moneyTo, amount);

                    PlayerUtils.sendFeedback(context, "cmd.money.pay.feedback");
                });
    }
}