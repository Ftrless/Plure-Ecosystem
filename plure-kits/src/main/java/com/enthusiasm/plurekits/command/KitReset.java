package com.enthusiasm.plurekits.command;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plurekits.data.DataManager;

public class KitReset implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targetPlayers = EntityArgumentType.getPlayers(context, "players");

        exec(
                context,
                context.getSource().getPlayer(),
                targetPlayers
        );

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, Collection<ServerPlayerEntity> targetPlayers) {
        for (ServerPlayerEntity player : targetPlayers) {
            DataManager.getInstance().getPlayerKitData(player).resetAllKits();
        }

        PlayerUtils.sendFeedback(
                context,
                "cmd.kit.reset-all.feedback",
                TextUtils.declensionWord(targetPlayers.size(), new String[]{"игрока", "игроков", "игроков"})
        );
    }
}
