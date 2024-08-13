package com.enthusiasm.plurechat.command;

import com.enthusiasm.plurechat.data.DataManager;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Ignore implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);

        if (targetPlayer == null) {
            return SINGLE_SUCCESS;
        }

        exec(context, senderPlayer, targetPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, ServerPlayerEntity targetPlayer) {
        boolean currentIgnoreState = DataManager.checkContainsIgnorableUser(senderPlayer.getUuid(), targetPlayer.getUuid());

        if (!currentIgnoreState) {
            DataManager.addOrUpdateIgnorableUser(senderPlayer.getUuid(), targetPlayer.getUuid());
            PlayerUtils.sendFeedback(context, "cmd.ignore.feedback.added", targetPlayer.getEntityName());
            return;
        }

        DataManager.removeIgnorableUser(senderPlayer.getUuid(), targetPlayer.getUuid());
        PlayerUtils.sendFeedback(context, "cmd.ignore.feedback.removed", targetPlayer.getEntityName());
    }
}
