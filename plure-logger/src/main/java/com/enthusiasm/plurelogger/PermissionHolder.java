package com.enthusiasm.plurelogger;

import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionHolder {
    public static final String ADMIN = "plure-logger.admin";
    public static final String LOGGER_SPY = "plure-logger.spy";
    public static final String ROLLBACK = "plure-logger.rollback";

    public static boolean checkPermission(ServerPlayerEntity player, String permission) {
        return Permissions.check(player, permission);
    }

    public static boolean checkPermission(ServerCommandSource sourceStack, String permission){
        if (!sourceStack.isExecutedByPlayer() && sourceStack.getName().equalsIgnoreCase("server")){
            return true;
        }

        return checkPermission(sourceStack.getPlayer(), permission);
    }
}
