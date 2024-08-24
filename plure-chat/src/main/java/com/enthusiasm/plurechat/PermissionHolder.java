package com.enthusiasm.plurechat;

import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfile;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionHolder {
    public enum Permission {
        IGNORE("ignore");

        private final String permissionString;

        Permission(String permissionString) {
            this.permissionString = "plurechat." + permissionString;
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
