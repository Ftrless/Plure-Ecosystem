package com.enthusiasm.plureutils;

import com.mojang.authlib.GameProfile;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class PermissionsHolder {
    public enum Permission {
        BYPASS_PLAYER_LIMIT("bypass-player-limit"),
        BYPASS_TYPE_WARP("warps.bypass-type-warp"),
        BYPASS_TYPE_HOME("homes.bypass-type-home"),
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
        GMS("gms"),
        GMC("gmc"),
        GMA("gma"),
        DAY("day"),
        NIGHT("night"),
        SUNNY("sunny"),
        VOTEDAY("voteday"),
        VOTESUN("votesun"),
        VOTE("vote"),
        HEAL("heal"),
        FEED("feed"),
        REPAIR("repair"),
        NIGHTVISION("nightvision"),
        RTP("rtp");

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

    public static CompletableFuture<Boolean> check(GameProfile src, Permission permission, int level, MinecraftServer server) {
        return Permissions.check(src, permission.getPermissionString(), level, server);
    }
}
