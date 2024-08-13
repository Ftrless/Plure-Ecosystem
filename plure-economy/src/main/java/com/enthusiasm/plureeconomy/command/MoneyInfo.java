package com.enthusiasm.plureeconomy.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plureeconomy.api.EconomyWrapper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

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
        EconomyWrapper
                .getPlayerMoney(targetPlayer)
                .thenAcceptAsync(playerMoney -> {
                    if (!forTarget) {
                        PlayerUtils.sendFeedback(context, "cmd.money.info.feedback", playerMoney);
                        return;
                    }

                    PlayerUtils.sendFeedback(context, "cmd.money.info.feedback.target", targetPlayer.getEntityName(), playerMoney);
                });
    }
}
