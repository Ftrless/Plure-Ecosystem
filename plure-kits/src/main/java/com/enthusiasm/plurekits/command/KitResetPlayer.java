package com.enthusiasm.plurekits.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plurekits.data.DataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class KitResetPlayer implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String kitName = StringArgumentType.getString(context, "kit_name");
        Collection<ServerPlayerEntity> targetPlayers = EntityArgumentType.getPlayers(context, "players");

        exec(
                context,
                context.getSource().getPlayer(),
                kitName,
                targetPlayers
        );

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, String kitName, Collection<ServerPlayerEntity> targetPlayers) {
        for (ServerPlayerEntity player : targetPlayers) {
            DataManager.getInstance().getPlayerKitData(player).resetKitCooldown(kitName);
        }

        PlayerUtils.sendFeedback(
                context,
                "cmd.kit.reset-kit.feedback",
                kitName,
                TextUtils.declensionWord(targetPlayers.size(), new String[]{"игрока", "игроков", "игроков"})
        );
    }
}