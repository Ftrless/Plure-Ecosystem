package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class God implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        boolean isGodModeEnabled = senderPlayer.getAbilities().invulnerable;

        if (isGodModeEnabled) {
            senderPlayer.getAbilities().invulnerable = false;
        } else {
            senderPlayer.getAbilities().invulnerable = true;
            senderPlayer.setHealth(senderPlayer.getMaxHealth());
        }

        senderPlayer.sendAbilitiesUpdate();

        PlayerUtils.sendFeedback(context, "cmd.god.feedback", TextUtils.translation(isGodModeEnabled ? "generic.disabled" : "generic.enabled", FormatUtils.Colors.FOCUS));
    }
}
