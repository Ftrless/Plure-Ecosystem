package com.enthusiasm.plurechat;

import com.enthusiasm.plurechat.command.Ignore;
import com.enthusiasm.plurecore.utils.suggestion.NickSuggestionProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.RootCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegistry {
    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess commandRegistryAccess,
            CommandManager.RegistrationEnvironment registrationEnvironment
    ) {
        RootCommandNode<ServerCommandSource> rootNode = dispatcher.getRoot();

        rootNode.addChild(literal("ignore")
                .requires(Permissions.require(PermissionHolder.Permission.IGNORE.getPermissionString(), 4))
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestionProvider.PROVIDER)
                        .executes(new Ignore())
                ).build()
        );
    }
}
