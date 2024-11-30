package com.enthusiasm.plureutils;

import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfile;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionsHolder {
    public enum Permission {
        BYPASS_PLAYER_LIMIT("bypass-player-limit"),
        BYPASS_TYPE_WARP("warps.bypass-type-warp"),
        BYPASS_TYPE_HOME("homes.bypass-type-home"),
        BYPASS_RTP_COOLDOWN("rtp.bypass-rtp-cooldown"),
        BYPASS_VANISH_VIEWING("vanish.bypass-viewing"),
        CREATE_WARP("warps.create"),
        INFO_WARP("warps.info"),
        ACCESS_LIST_WARPS("warps.access"),
        TP_WARP("warps.tp"),
        DELETE_WARP("warps.delete"),
        INVITE_WARP("warps.invite"),
        DE_INVITE_WARP("warps.de-invite"),
        TRANSFER_WARP("warps.transfer"),
        LIST_WARPS("warps.list"),
        SET_HOME("homes.set"),
        TP_HOME("homes.tp"),
        DELETE_HOME("homes.delete"),
        PLAYTIME("playtime"),
        PLAYTIME_TARGET("playtime.target"),
        PLAYTIME_TOP("playtime.top"),
        SET_SPAWN("spawn.set"),
        TP_SPAWN("spawn.tp"),
        FORCE_SPAWN("spawn.force"),
        TPA("tpa"),
        TPAHERE("tpahere"),
        TPAACCEPT("tpaaccept"),
        TPADENY("tpadeny"),
        GMS("gms"),
        GMC("gmc"),
        GMA("gma"),
        DAY("day"),
        NIGHT("night"),
        SUNNY("sunny"),
        VOTEDAY("voteday"),
        VOTESUN("votesun"),
        VOTE("vote"),
        FLY("fly"),
        GOD("god"),
        ANVIL("anvil"),
        WORKBENCH("workbench"),
        GRINDSTONE("grindstone"),
        TRASHBIN("trashbin"),
        ENDERCHEST("enderchest"),
        HEAL("heal"),
        FEED("feed"),
        REPAIR("repair"),
        NIGHTVISION("nightvision"),
        RTP("rtp"),
        TPPOS("tppos"),
        VANISH("vanish"),
        RESTART_AT("restart-at"),
        FORCE_RESTART("force-restart"),
        POSTPONE_RESTART("postpone-restart"),
        VIEW_INV("view.inv"),
        VIEW_ECHEST("view.echest"),
        KEEP_INV("keep-inv");

        private final String permissionString;

        Permission(String permissionString) {
            this.permissionString = "plureutils." + permissionString;
        }

        public String getPermissionString() {
            return permissionString;
        }
    }

    public static boolean check(ServerPlayerEntity src, Permission permission, int level) {
        return Permissions.check(src, permission.getPermissionString(), level);
    }

    public static boolean check(ServerCommandSource src, Permission permission, int level) {
        return Permissions.check(src, permission.getPermissionString(), level);
    }

    public static CompletableFuture<Boolean> check(GameProfile src, Permission permission, int level, MinecraftServer server) {
        return Permissions.check(src, permission.getPermissionString(), level, server);
    }
}
