package com.enthusiasm.plurekits;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurekits.command.*;
import com.enthusiasm.plurekits.suggestion.KitSuggestion;

public class CommandRegistry {
    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess commandRegistryAccess,
            CommandManager.RegistrationEnvironment registrationEnvironment
    ) {
        CommandNode<ServerCommandSource> kitNode = dispatcher.register(literal("kit"));

        kitNode.addChild(literal("add")
                .requires(Permissions.require(PermissionHolder.Permission.ADD.getPermissionString(), 4))
                .then(argument("kit_name", StringArgumentType.word())
                        .then(argument("cooldown", StringArgumentType.word())
                                .then(argument("needs_played", StringArgumentType.word())
                                        .executes(new KitAdd())
                                ))
                ).build()
        );

        kitNode.addChild(literal("remove")
                .requires(Permissions.require(PermissionHolder.Permission.REMOVE.getPermissionString(), 4))
                .then(argument("kit_name", StringArgumentType.word())
                        .suggests(KitSuggestion.ALL_KITS_SUGGESTION_PROVIDER)
                        .executes(new KitRemove())
                ).build()
        );

        kitNode.addChild(literal("claim")
                .requires(Permissions.require(PermissionHolder.Permission.CLAIM.getPermissionString(), 4))
                .executes(new KitAll())
                .then(argument("kit_name", StringArgumentType.word())
                        .suggests(KitSuggestion.CLAIMABLE_KITS_SUGGESTION_PROVIDER)
                        .executes(new KitClaim())
                ).build()
        );

        kitNode.addChild(literal("resetPlayerKit")
                .requires(Permissions.require(PermissionHolder.Permission.RESET_PER_KIT.getPermissionString(), 4))
                .then(argument("players", EntityArgumentType.players())
                        .then(argument("kit_name", StringArgumentType.word())
                                .suggests(KitSuggestion.ALL_KITS_SUGGESTION_PROVIDER)
                                .executes(new KitResetPlayer())
                        )
                ).build()
        );

        kitNode.addChild(literal("resetPlayer")
                .requires(Permissions.require(PermissionHolder.Permission.RESET_PER_PLAYER.getPermissionString(), 4))
                .then(argument("players", EntityArgumentType.players())
                        .executes(new KitReset())
                ).build()
        );

        kitNode.addChild(literal("view")
                .requires(Permissions.require(PermissionHolder.Permission.VIEW.getPermissionString(), 4))
                .then(argument("kit_name", StringArgumentType.word())
                        .suggests(KitSuggestion.ALL_KITS_SUGGESTION_PROVIDER)
                        .executes(new KitView())
                ).build()
        );

        kitNode.addChild(literal("reload")
                .requires(Permissions.require(PermissionHolder.Permission.RELOAD.getPermissionString(), 4))
                .executes(new KitReload()).build()
        );
    }
}
