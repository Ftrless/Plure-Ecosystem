package com.enthusiasm.plureeconomy;

import com.enthusiasm.plurecore.utils.suggestion.NickSuggestionProvider;
import com.enthusiasm.plureeconomy.command.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
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
        CommandNode<ServerCommandSource> moneyNode = dispatcher.register(literal("money"));

        moneyNode.addChild(literal("info")
                .requires(Permissions.require(PermissionHolder.Permission.INFO.getPermissionString(), 4))
                .executes(new MoneyInfo())
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestionProvider.PROVIDER)
                        .executes(new MoneyInfo()::runForTarget)
                ).build()
        );

        moneyNode.addChild(literal("pay")
                .requires(Permissions.require(PermissionHolder.Permission.PAY.getPermissionString(), 4))
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestionProvider.PROVIDER)
                        .then(argument("amount", DoubleArgumentType.doubleArg(0.01))
                                .executes(new MoneyPay())
                        )
                ).build()
        );

        moneyNode.addChild(literal("add")
                .requires(Permissions.require(PermissionHolder.Permission.ADD.getPermissionString(), 4))
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestionProvider.PROVIDER)
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(new MoneyAdd())
                        )
                ).build()
        );

        moneyNode.addChild(literal("take")
                .requires(Permissions.require(PermissionHolder.Permission.TAKE.getPermissionString(), 4))
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestionProvider.PROVIDER)
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(new MoneyTake())
                        )
                ).build()
        );

        moneyNode.addChild(literal("set")
                .requires(Permissions.require(PermissionHolder.Permission.SET.getPermissionString(), 4))
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestionProvider.PROVIDER)
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(new MoneySet())
                        )
                ).build()
        );

        moneyNode.addChild(literal("top")
                .requires(Permissions.require(PermissionHolder.Permission.TOP.getPermissionString(), 4))
                .executes(new MoneyTop())
                .build()
        );
    }
}
