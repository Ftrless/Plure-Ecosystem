package com.enthusiasm.plurekits;

import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfile;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionHolder {
    public enum Permission {
        CLAIM("claim"),
        ADD("add"),
        REMOVE("remove"),
        RESET_PER_PLAYER("reset-player"),
        RESET_PER_KIT("reset-kit"),
        RELOAD("reload"),
        VIEW("view");

        private final String permissionString;

        Permission(String permissionString) {
            this.permissionString = "plurekits." + permissionString;
        }

        public String getPermissionString() {
            return permissionString;
        }
    }

    public static String getKitPermission(String kitName) {
        return Permission.CLAIM.getPermissionString() + "." + kitName;
    }

    public static boolean check(ServerPlayerEntity src, Permission permission, int level) {
        return Permissions.check(src, permission.getPermissionString(), level);
    }

    public static boolean check(ServerPlayerEntity src, String permission, int level) {
        return Permissions.check(src, permission, level);
    }

    public static CompletableFuture<Boolean> check(GameProfile src, Permission permission, int level, MinecraftServer server) {
        return Permissions.check(src, permission.getPermissionString(), level, server);
    }
}
