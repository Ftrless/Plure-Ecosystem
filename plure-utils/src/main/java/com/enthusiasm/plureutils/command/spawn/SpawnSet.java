package com.enthusiasm.plureutils.command.spawn;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.spawn.SpawnDataManager;

public class SpawnSet implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        SpawnDataManager spawnDataManager = DataManager.getSpawnDataManager();

        spawnDataManager.setSpawn(senderPlayer);

        PlayerUtils.sendFeedback(context, "cmd.spawn.set.feedback");
    }
}
