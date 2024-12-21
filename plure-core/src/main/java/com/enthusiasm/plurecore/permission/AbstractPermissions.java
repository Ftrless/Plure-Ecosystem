package com.enthusiasm.plurecore.permission;

import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractPermissions {
    public abstract String getPermissionPrefix();

    public boolean checkPermission(ServerPlayerEntity player, String permission) {
        return Permissions.check(player, buildNode(permission));
    }

    public boolean checkPermission(ServerCommandSource sourceStack, String permission, AbstractPermissions permissions) {
        if (!sourceStack.isExecutedByPlayer() || sourceStack.getName().equalsIgnoreCase("server")) {
            return true;
        }

        return permissions.checkPermission(sourceStack.getPlayer(), permission);
    }

    private String buildNode(String permission) {
        return getPermissionPrefix() + "." + permission;
    }
}
