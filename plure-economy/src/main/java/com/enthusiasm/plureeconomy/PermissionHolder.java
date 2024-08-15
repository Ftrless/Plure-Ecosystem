package com.enthusiasm.plureeconomy;

import com.mojang.authlib.GameProfile;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class PermissionHolder {
    public enum Permission {
        INFO("claim"),
        ADD("add"),
        TAKE("remove"),
        SET("reset-player"),
        PAY("reset-kit"),
        TOP("reload");

        private final String permissionString;

        Permission(String permissionString) {
            this.permissionString = "plureeconomy." + permissionString;
        }

        public String getPermissionString() {
            return permissionString;
        }
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
