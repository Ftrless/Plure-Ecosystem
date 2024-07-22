package com.enthusiasm.plureutils.command.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class NightVision implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        boolean hasNightVisionEffect = senderPlayer.hasStatusEffect(StatusEffect.byRawId(16));

        Message isEnabledText = TextUtils.translation( !hasNightVisionEffect ? "generic.enabled" : "generic.disabled", FormatUtils.Colors.FOCUS);

        if (!hasNightVisionEffect) {
            senderPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        } else {
            senderPlayer.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }

        PlayerUtils.sendFeedback(context, "cmd.nv.feedback", isEnabledText);
    }
}
