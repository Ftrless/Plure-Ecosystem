package com.enthusiasm.plureutils.command.spawn;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.WorldUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.spawn.SpawnData;
import com.enthusiasm.plureutils.data.spawn.SpawnDataManager;

public class SpawnForce implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);

        exec(context, senderPlayer, targetPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, ServerPlayerEntity targetPlayer) throws CommandSyntaxException {
        SpawnDataManager spawnDataManager = DataManager.getSpawnDataManager();
        SpawnData spawnData = spawnDataManager.getSpawn();

        Message notExist = TextUtils.translation("cmd.spawn.force.error.not-exist", FormatUtils.Colors.ERROR);
        Message notFound = TextUtils.translation("cmd.spawn.force.error.not-found", FormatUtils.Colors.ERROR);

        if (spawnData == null) {
            throw CommandHelper.createException(notExist);
        }

        if (targetPlayer == null) {
            throw CommandHelper.createException(notFound);
        }

        PlayerUtils.teleportPlayer(
                targetPlayer,
                spawnData.x,
                spawnData.y,
                spawnData.z,
                spawnData.yaw,
                spawnData.pitch,
                WorldUtils.getServerWorld(
                        spawnData.world,
                        senderPlayer.server
                )
        );

        PlayerUtils.sendFeedback(context, "cmd.forcespawn.feedback", targetPlayer.getName().getString());
    }
}
