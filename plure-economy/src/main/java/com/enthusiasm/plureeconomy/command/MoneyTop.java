package com.enthusiasm.plureeconomy.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plureeconomy.api.EconomyWrapper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.atomic.AtomicInteger;

public class MoneyTop implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        EconomyWrapper
                .getMoneyTop()
                .thenAcceptAsync(moneyTop -> {
                    PlayerUtils.sendFeedback(context, "cmd.money.top.header");

                    AtomicInteger count = new AtomicInteger(1);

                    moneyTop.forEach((playerName, money) -> {
                        PlayerUtils.sendFeedback(context, "cmd.money.top.element", count, playerName, money);

                        count.addAndGet(1);
                    });
                });
    }
}
