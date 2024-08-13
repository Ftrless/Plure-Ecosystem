package com.enthusiasm.plureeconomy.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plureeconomy.api.EconomyActions;
import com.enthusiasm.plureeconomy.api.EconomyWrapper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MoneyAdd implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);
        double amount = DoubleArgumentType.getDouble(context, "amount");

        exec(context, targetPlayer, amount);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity targetPlayer, double amount) throws CommandSyntaxException {
        EconomyWrapper
                .getPlayerMoney(targetPlayer)
                .thenAcceptAsync(playerMoney -> {
                    EconomyWrapper.updatePlayerMoney(targetPlayer, playerMoney, amount, EconomyActions.ADD);
                    PlayerUtils.sendFeedback(context, "cmd.money.add.feedback", targetPlayer.getEntityName(), amount);
                });
    }
}
