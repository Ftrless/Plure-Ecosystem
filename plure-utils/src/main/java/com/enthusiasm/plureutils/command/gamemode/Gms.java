package com.enthusiasm.plureutils.command.gamemode;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;

public class Gms implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        Message alreadySurvival = TextUtils.translation("cmd.gms.error.already_survival", FormatUtils.Colors.ERROR);

        if (senderPlayer.interactionManager.getGameMode() == GameMode.SURVIVAL) {
            throw CommandHelper.createException(alreadySurvival);
        }

        senderPlayer.changeGameMode(GameMode.SURVIVAL);

        PlayerUtils.sendFeedback(context, "cmd.gm.feedback", "выживания");
    }
}