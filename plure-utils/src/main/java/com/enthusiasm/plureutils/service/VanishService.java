package com.enthusiasm.plureutils.service;

import com.enthusiasm.plureutils.PermissionsHolder;
import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.data.vanish.VanishData;
import com.enthusiasm.plureutils.util.VanishedEntity;
import com.mojang.authlib.GameProfile;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class VanishService {
    public static final ThreadLocal<Entity> ACTIVE_ENTITY = ThreadLocal.withInitial(() -> null);
    public static final Predicate<Entity> NO_SPECTATORS_AND_NO_VANISH = EntityPredicates.EXCEPT_SPECTATOR.and(entity -> !VanishService.isVanished(entity));
    public static final Predicate<Entity> CAN_BE_COLLIDED_WITH_AND_NO_VANISH = NO_SPECTATORS_AND_NO_VANISH.and(Entity::isCollidable);

    public static final PlayerDataStorage<VanishData> VANISH_DATA_STORAGE = new JsonDataStorage<>("vanish", VanishData.class);

    public static final Identifier VANISH_BAR = new Identifier("minecraft", "vanish_bar");
    public static final Text VANISH_BAR_COMPONENT = Text.literal("Вы в невидимости!").setStyle(Style.EMPTY.withColor(Formatting.WHITE));
    public static CommandBossBar VANISH_BOSS_BAR;

    public static void onInitialize() {
        BossBarManager bossBarManager = PlureUtilsEntrypoint.SERVER.getBossBarManager();
        CommandBossBar possibleBar = bossBarManager.get(VANISH_BAR);

        if (possibleBar == null) {
            VANISH_BOSS_BAR = bossBarManager.add(VANISH_BAR, VANISH_BAR_COMPONENT);
            VANISH_BOSS_BAR.setColor(BossBar.Color.WHITE);
            VANISH_BOSS_BAR.setValue(100);
            VANISH_BOSS_BAR.setStyle(BossBar.Style.NOTCHED_20);
            VANISH_BOSS_BAR.setMaxValue(100);
        } else {
            VANISH_BOSS_BAR = possibleBar;
        }

        PlayerDataApi.register(VANISH_DATA_STORAGE);
    }

    public static List<ServerPlayerEntity> getVisiblePlayers(@NotNull ServerCommandSource observer) {
        MinecraftServer server = observer.getServer();
        ObjectArrayList<ServerPlayerEntity> list = new ObjectArrayList<>();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (canSeePlayer(player, observer)) {
                list.add(player);
            }
        }

        return list;
    }

    public static boolean canSeePlayer(ServerPlayerEntity actor, ServerCommandSource observer) {
        if (isVanished(actor)) {
            if (observer.getEntity() != null && actor.equals(observer.getEntity())) {
                return true;
            } else {
                return canViewVanished(observer);
            }
        } else {
            return true;
        }
    }

    public static boolean canSeePlayer(MinecraftServer server, UUID actor, ServerCommandSource observer) {
        if (isVanished(server, actor)) {
            if (observer.getEntity() != null && actor.equals(observer.getEntity().getUuid())) {
                return true;
            } else {
                return canViewVanished(observer);
            }
        } else {
            return true;
        }
    }

    public static boolean isVanished(Entity entity) {
        if (entity instanceof VanishedEntity vanishedEntity) {
            return vanishedEntity.isVanished();
        }

        return false;
    }

    public static boolean isVanished(MinecraftServer server, UUID uuid) {
        VanishData data = PlayerDataApi.getCustomDataFor(server, uuid, VANISH_DATA_STORAGE);

        return data != null && data.vanished;
    }

    public static boolean setVanished(GameProfile profile, MinecraftServer server, boolean vanish) {
        if (isVanished(server, profile.getId()) == vanish) {
            return false;
        }

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(profile.getId());
        boolean isOnline = player != null;

        if (vanish && isOnline) {
            vanish(player);
        }

        VanishData data = PlayerDataApi.getCustomDataFor(server, profile.getId(), VANISH_DATA_STORAGE);
        if (data == null) {
            data = new VanishData();
        }

        data.vanished = vanish;
        PlayerDataApi.setCustomDataFor(server, profile.getId(), VANISH_DATA_STORAGE, data);

        if (isOnline) {
            ((VanishedEntity) player).markDirty();
        }

        if (!vanish && isOnline) {
            unVanish(player);
        }

        if (isOnline) {
            server.forcePlayerSampleUpdate();
        }

        return true;
    }

    private static void vanish(ServerPlayerEntity actor) {
        broadcastToOthers(actor, new PlayerRemoveS2CPacket(Collections.singletonList(actor.getUuid())));
        VANISH_BOSS_BAR.addPlayer(actor);
    }

    private static void unVanish(ServerPlayerEntity actor) {
        broadcastToOthers(actor, PlayerListS2CPacket.entryFromPlayer(Collections.singletonList(actor)));
        VANISH_BOSS_BAR.removePlayer(actor);
    }

    public static boolean canViewVanished(ServerCommandSource observer) {
        return PermissionsHolder.check(observer, PermissionsHolder.Permission.BYPASS_VANISH_VIEWING, 4);
    }

    private static void broadcastToOthers(ServerPlayerEntity actor, Packet<?> packet) {
        for (ServerPlayerEntity observer : actor.server.getPlayerManager().getPlayerList()) {
            if (!VanishService.canViewVanished(observer.getCommandSource()) && !observer.equals(actor)) {
                observer.networkHandler.sendPacket(packet);
            }
        }
    }
}
