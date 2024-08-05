package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Fly implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        boolean canFly = senderPlayer.getAbilities().allowFlying;

        if (canFly) {
            senderPlayer.getAbilities().allowFlying = false;
            senderPlayer.getAbilities().flying = false;
        } else {
            senderPlayer.getAbilities().allowFlying = true;
        }

        senderPlayer.sendAbilitiesUpdate();

        PlayerUtils.sendFeedback(context, "cmd.fly.feedback", TextUtils.translation(canFly ? "generic.disabled" : "generic.enabled", FormatUtils.Colors.FOCUS));
    }
}
