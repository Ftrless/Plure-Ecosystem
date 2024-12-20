package com.enthusiasm.plurelogger.command;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurelogger.PermissionHolder;
import com.enthusiasm.plurelogger.command.commands.*;
import com.enthusiasm.plurelogger.utils.BrigadierUtils;

public class CommandRegistry {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(CommandRegistry::onCommandRegister);
    }

    private static void onCommandRegister(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        RootCommandNode<ServerCommandSource> rootNode = dispatcher.getRoot();

        LiteralCommandNode<ServerCommandSource> loggerNode = literal("logger")
                .requires(serverCommandSource -> PermissionHolder.checkPermission(serverCommandSource, PermissionHolder.LOGGER_SPY))
                .build();
        LiteralCommandNode<ServerCommandSource> shortenLoggerNode = literal("l").redirect(loggerNode)
                .requires(serverCommandSource -> PermissionHolder.checkPermission(serverCommandSource, PermissionHolder.LOGGER_SPY))
                .build();

        rootNode.addChild(loggerNode);
        rootNode.addChild(shortenLoggerNode);

        loggerNode.addChild(new InspectCommand().build());
        loggerNode.addChild(BrigadierUtils.buildRedirect("i", new InspectCommand().build()));

        loggerNode.addChild(new SearchCommand().build());
        loggerNode.addChild(BrigadierUtils.buildRedirect("s", new SearchCommand().build()));

        loggerNode.addChild(new PageCommand().build());
        loggerNode.addChild(BrigadierUtils.buildRedirect("pg", new PageCommand().build()));

        loggerNode.addChild(new RollbackCommand().build());
        loggerNode.addChild(BrigadierUtils.buildRedirect("rb", new RollbackCommand().build()));

        loggerNode.addChild(new PreviewCommand().build());
        loggerNode.addChild(BrigadierUtils.buildRedirect("pv", new PreviewCommand().build()));

        loggerNode.addChild(new RestoreCommand().build());

        loggerNode.addChild(new StatusCommand().build());

        loggerNode.addChild(new TeleportCommand().build());

        loggerNode.addChild(new PurgeCommand().build());

        loggerNode.addChild(new PlayerCommand().build());

        loggerNode.addChild(new ForceDrainCommand().build());
    }
}
