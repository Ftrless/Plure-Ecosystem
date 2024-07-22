package com.enthusiasm.plureutils.command.gamemode;

import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class Gmc implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        Message alreadyCreative = TextUtils.translation("cmd.gmc.error.already_creative", FormatUtils.Colors.ERROR);

        if (senderPlayer.isCreative()) {
            throw CommandHelper.createException(alreadyCreative);
        }

        senderPlayer.changeGameMode(GameMode.CREATIVE);

        PlayerUtils.sendFeedback(context, "cmd.gm.feedback", "creative");
    }
}
