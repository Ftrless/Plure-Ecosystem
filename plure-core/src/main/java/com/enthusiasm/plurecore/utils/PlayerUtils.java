package com.enthusiasm.plurecore.utils;

import java.util.Optional;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Dynamic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;

public class PlayerUtils {
    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) {
        String requestedName = StringArgumentType.getString(context, "target_player");

        if (requestedName == null) {
            return null;
        }

        MinecraftServer server = context.getSource().getServer();
        ServerPlayerEntity requestedPlayer = server.getPlayerManager().getPlayer(requestedName);

        if (requestedPlayer != null) {
            return requestedPlayer;
        }

        return getOfflinePlayer(requestedName, server);
    }

    private static ServerPlayerEntity getOfflinePlayer(String playerName, MinecraftServer server) {
        Optional<UUID> userUUID = CacheService.getUserByNickname(playerName);

        if (userUUID.isEmpty()) {
            return null;
        }

        GameProfile gameProfile = CacheService.getUserProfile(userUUID.get());

        ServerPlayerEntity player = server.getPlayerManager().createPlayer(gameProfile);
        NbtCompound compound = server.getPlayerManager().loadPlayerData(player);

        if (compound != null) {
            var dimensionNbt = new Dynamic<>(NbtOps.INSTANCE, compound.get("Dimension"));

            Optional<RegistryKey<World>> world = DimensionType.worldFromDimensionNbt(dimensionNbt)
                    .result();
            world.ifPresent(worldRegistryKey -> player.setServerWorld(
                    server.getWorld(worldRegistryKey)
            ));
        }

        return player;
    }

    public static void teleportPlayer(ServerPlayerEntity senderPlayer, double x, double y, double z, float yaw, float pitch, ServerWorld targetWorld) {
        if (senderPlayer.isAlive()) {
            senderPlayer.teleport(targetWorld, x, y, z, yaw, pitch);
            senderPlayer.networkHandler.syncWithPlayerPosition();
        }
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> ctx, String key, Object... args) {
        ctx.getSource().sendFeedback(() -> TextUtils.translation(key, FormatUtils.Colors.DEFAULT, args), false);
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> ctx, Text feedback) {
        ctx.getSource().sendFeedback(() -> feedback, false);
    }
}
