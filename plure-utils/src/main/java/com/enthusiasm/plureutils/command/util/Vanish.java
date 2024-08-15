package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.service.VanishService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Vanish implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return Command.SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) {
        boolean vanished = VanishService.isVanished(senderPlayer);

        VanishService.setVanished(senderPlayer.getGameProfile(), senderPlayer.getServer(), !vanished);
        PlayerUtils.sendFeedback(context, "cmd.vanish.feedback", TextUtils.translation(vanished ? "generic.disabled" : "generic.enabled", FormatUtils.Colors.FOCUS));
    }
}
