package com.enthusiasm.plurekits;

import com.enthusiasm.plurekits.command.*;
import com.enthusiasm.plurekits.suggestion.KitSuggestion;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
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
        CommandNode<ServerCommandSource> kitNode = dispatcher.register(literal("kit"));

        kitNode.addChild(
                literal("add")
                        .then(argument("kit_name", StringArgumentType.word())
                                .then(argument("cooldown", StringArgumentType.word())
                                        .then(argument("needs_played", StringArgumentType.word())
                                                .executes(new KitAdd())
                                        ))
                        ).build()
        );

        kitNode.addChild(literal("remove")
                .then(argument("kit_name", StringArgumentType.word())
                        .suggests(KitSuggestion.ALL_KITS_SUGGESTION_PROVIDER)
                        .executes(new KitRemove())
                ).build()
        );

        kitNode.addChild(literal("claim")
                .executes(new KitAll())
                .then(argument("kit_name", StringArgumentType.word())
                        .suggests(KitSuggestion.CLAIMABLE_KITS_SUGGESTION_PROVIDER)
                        .executes(new KitClaim())
                ).build()
        );

        kitNode.addChild(literal("resetPlayerKit")
                .then(argument("players", EntityArgumentType.players())
                        .then(argument("kit_name", StringArgumentType.word())
                                .suggests(KitSuggestion.ALL_KITS_SUGGESTION_PROVIDER)
                                .executes(new KitResetPlayer())
                        )
                ).build()
        );

        kitNode.addChild(literal("resetPlayer")
                .then(argument("players", EntityArgumentType.players())
                        .executes(new KitReset())
                ).build()
        );

        kitNode.addChild(literal("view")
                .then(argument("kit_name", StringArgumentType.word())
                        .suggests(KitSuggestion.ALL_KITS_SUGGESTION_PROVIDER)
                        .executes(new KitView())
                ).build()
        );

        kitNode.addChild(literal("reload")
                .executes(new KitReload()).build()
        );
    }
}
