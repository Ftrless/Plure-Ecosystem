package com.enthusiasm.plureutils;

import com.enthusiasm.plureutils.command.gamemode.Gma;
import com.enthusiasm.plureutils.command.gamemode.Gmc;
import com.enthusiasm.plureutils.command.gamemode.Gms;
import com.enthusiasm.plureutils.command.home.HomeDelete;
import com.enthusiasm.plureutils.command.home.HomeSet;
import com.enthusiasm.plureutils.command.home.HomeTp;
import com.enthusiasm.plureutils.command.playtime.Playtime;
import com.enthusiasm.plureutils.command.playtime.PlaytimeTop;
import com.enthusiasm.plureutils.command.spawn.SpawnForce;
import com.enthusiasm.plureutils.command.spawn.SpawnSet;
import com.enthusiasm.plureutils.command.spawn.SpawnTp;
import com.enthusiasm.plureutils.command.tpa.Tpa;
import com.enthusiasm.plureutils.command.tpa.TpaAccept;
import com.enthusiasm.plureutils.command.tpa.TpaDeny;
import com.enthusiasm.plureutils.command.tpa.TpaHere;
import com.enthusiasm.plureutils.command.util.*;
import com.enthusiasm.plureutils.command.util.screens.*;
import com.enthusiasm.plureutils.command.vote.Vote;
import com.enthusiasm.plureutils.command.vote.VoteDay;
import com.enthusiasm.plureutils.command.vote.VoteSun;
import com.enthusiasm.plureutils.command.warp.*;
import com.enthusiasm.plureutils.command.weather.Day;
import com.enthusiasm.plureutils.command.weather.Night;
import com.enthusiasm.plureutils.command.weather.Sunny;
import com.enthusiasm.plureutils.util.suggetion.NickSuggestion;
import com.enthusiasm.plureutils.util.suggetion.VoteSuggestion;
import com.enthusiasm.plureutils.util.suggetion.WarpSuggestion;
import com.enthusiasm.plureutils.util.suggetion.WorldSuggestion;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class CommandRegistry {
    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess commandRegistryAccess,
            RegistrationEnvironment registrationEnvironment
    ) {
        CommandNode<ServerCommandSource> warpNode = dispatcher.register(literal("warp"));
        CommandNode<ServerCommandSource> homeNode = dispatcher.register(literal("home"));
        CommandNode<ServerCommandSource> spawnNode = dispatcher.register(literal("spawn"));
        RootCommandNode<ServerCommandSource> rootNode = dispatcher.getRoot();

        registerWarpCommands(warpNode);
        registerHomeCommands(homeNode);
        registerSpawnCommands(spawnNode);
        registerTpaCommands(rootNode);
        registerPlaytimeCommands(rootNode);
        registerWeatherCommands(rootNode);
        registerGamemodeCommands(rootNode);
        registerVoteCommands(rootNode);
        registerUtilCommands(rootNode);
        registerScreensCommands(rootNode);
    }

    public static void registerWarpCommands(CommandNode<ServerCommandSource> node) {
        node.addChild(literal("create")
                .requires(Permissions.require(PermissionsHolder.Permission.CREATE_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .then(argument("global", StringArgumentType.word())
                                .suggests(WarpSuggestion.WARP_TYPE)
                                .executes(new WarpCreate())
                        )).build()
        );

        node.addChild(literal("info")
                .requires(Permissions.require(PermissionsHolder.Permission.INFO_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .suggests(WarpSuggestion.PERM_LIST_SUGGESTION_PROVIDER)
                        .executes(new WarpInfo())
                ).build()
        );

        node.addChild(literal("delete")
                .requires(Permissions.require(PermissionsHolder.Permission.DELETE_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .suggests(WarpSuggestion.LIST_SUGGESTION_PROVIDER)
                        .executes(new WarpDelete())
                ).build()
        );

        node.addChild(literal("tp")
                .requires(Permissions.require(PermissionsHolder.Permission.TP_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .suggests(WarpSuggestion.LIST_SUGGESTION_PROVIDER)
                        .executes(new WarpTp())
                ).build()
        );

        node.addChild(literal("invite")
                .requires(Permissions.require(PermissionsHolder.Permission.INVITE_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .suggests(WarpSuggestion.LIST_SUGGESTION_PROVIDER)
                        .then(argument("target_player", StringArgumentType.word())
                                .suggests(NickSuggestion.NICK_SUGGESTION_PROVIDER)
                                .executes(new WarpInvite()))
                ).build()
        );

        node.addChild(literal("revoke-invite")
                .requires(Permissions.require(PermissionsHolder.Permission.DE_INVITE_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .suggests(WarpSuggestion.LIST_SUGGESTION_PROVIDER)
                        .then(argument("target_player", StringArgumentType.word())
                                .suggests(NickSuggestion.NICK_SUGGESTION_PROVIDER)
                                .executes(new WarpRevokeInvite()))
                ).build()
        );

        node.addChild(literal("transfer")
                .requires(Permissions.require(PermissionsHolder.Permission.TRANSFER_WARP.getPermissionString(), 4))
                .then(argument("warp_name", StringArgumentType.word())
                        .suggests(WarpSuggestion.LIST_SUGGESTION_PROVIDER)
                        .then(argument("target_player", StringArgumentType.word())
                                .suggests(NickSuggestion.NICK_SUGGESTION_PROVIDER)
                                .executes(new WarpTransfer()))
                ).build()
        );

        node.addChild(literal("list")
                .requires(Permissions.require(PermissionsHolder.Permission.LIST_WARPS.getPermissionString(), 4))
                .executes(new WarpList())
                .build()
        );

        node.addChild(literal("access")
                .requires(Permissions.require(PermissionsHolder.Permission.ACCESS_LIST_WARPS.getPermissionString(), 4))
                .executes(new WarpAccess())
                .build()
        );
    }

    public static void registerHomeCommands(CommandNode<ServerCommandSource> node) {
        node.addChild(literal("set")
                .requires(Permissions.require(PermissionsHolder.Permission.SET_HOME.getPermissionString(), 4))
                .executes(new HomeSet())
                .build()
        );

        node.addChild(literal("tp")
                .requires(Permissions.require(PermissionsHolder.Permission.TP_HOME.getPermissionString(), 4))
                .executes(new HomeTp())
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestion.NICK_SUGGESTION_PROVIDER)
                        .executes(new HomeTp()::runForPlayer))
                .build()
        );

        node.addChild(literal("delete")
                .requires(Permissions.require(PermissionsHolder.Permission.DELETE_HOME.getPermissionString(), 4))
                .executes(new HomeDelete())
                .build()
        );
    }

    public static void registerSpawnCommands(CommandNode<ServerCommandSource> node) {
        node.addChild(literal("set")
                .requires(Permissions.require(PermissionsHolder.Permission.SET_SPAWN.getPermissionString(), 4))
                .executes(new SpawnSet())
                .build()
        );

        node.addChild(literal("tp")
                .requires(Permissions.require(PermissionsHolder.Permission.TP_SPAWN.getPermissionString(), 4))
                .executes(new SpawnTp())
                .build()
        );

        node.addChild(literal("force")
                .requires(Permissions.require(PermissionsHolder.Permission.FORCE_SPAWN.getPermissionString(), 4))
                .then(argument("target_player", StringArgumentType.word())
                        .suggests(NickSuggestion.NICK_SUGGESTION_PROVIDER)
                        .executes(new SpawnForce())
                ).build()
        );
    }

    public static void registerTpaCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("tpa")
                .requires(Permissions.require(PermissionsHolder.Permission.TPA.getPermissionString(),4))
                .then(argument("target_player", EntityArgumentType.player())
                        .executes(new Tpa())
                ).build()
        );

        node.addChild(literal("tpahere")
                .requires(Permissions.require(PermissionsHolder.Permission.TPAHERE.getPermissionString(),4))
                .then(argument("target_player", EntityArgumentType.player())
                        .executes(new TpaHere())
                ).build()
        );

        node.addChild(literal("tpaaccept")
                .requires(Permissions.require(PermissionsHolder.Permission.TPAACCEPT.getPermissionString(),4))
                .executes(new TpaAccept()::runAuto)
                .then(argument("target_player", EntityArgumentType.player())
                        .executes(new TpaAccept())
                ).build()
        );

        node.addChild(literal("tpadeny")
                .requires(Permissions.require(PermissionsHolder.Permission.TPADENY.getPermissionString(),4))
                .executes(new TpaDeny()::runAuto)
                .then(argument("target_player", EntityArgumentType.player())
                        .executes(new TpaDeny())
                ).build()
        );
    }

    public static void registerPlaytimeCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("playtime")
                .requires(Permissions.require(PermissionsHolder.Permission.PLAYTIME.getPermissionString(), 4))
                .executes(new Playtime())
                .then(argument("target_player", StringArgumentType.word())
                        .requires(Permissions.require(PermissionsHolder.Permission.PLAYTIME_TARGET.getPermissionString(), 4))
                        .suggests(NickSuggestion.NICK_SUGGESTION_PROVIDER)
                        .executes(new Playtime()::runForTargetPlayer)
                ).build()
        );

        node.addChild(literal("playtime-top")
                .requires(Permissions.require(PermissionsHolder.Permission.PLAYTIME_TOP.getPermissionString(), 4))
                .executes(new PlaytimeTop())
                .build()
        );
    }

    public static void registerWeatherCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("day")
                .requires(Permissions.require(PermissionsHolder.Permission.DAY.getPermissionString(), 4))
                .executes(new Day())
                .build()
        );

        node.addChild(literal("night")
                .requires(Permissions.require(PermissionsHolder.Permission.NIGHT.getPermissionString(), 4))
                .executes(new Night())
                .build()
        );

        node.addChild(literal("sunny")
                .requires(Permissions.require(PermissionsHolder.Permission.SUNNY.getPermissionString(), 4))
                .executes(new Sunny())
                .build()
        );
    }

    public static void registerGamemodeCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("gms")
                .requires(Permissions.require(PermissionsHolder.Permission.GMS.getPermissionString(), 4))
                .executes(new Gms())
                .build()
        );

        node.addChild(literal("gmc")
                .requires(Permissions.require(PermissionsHolder.Permission.GMC.getPermissionString(), 4))
                .executes(new Gmc())
                .build()
        );

        node.addChild(literal("gma")
                .requires(Permissions.require(PermissionsHolder.Permission.GMA.getPermissionString(), 4))
                .executes(new Gma())
                .build()
        );
    }

    public static void registerVoteCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("voteday")
                .requires(Permissions.require(PermissionsHolder.Permission.VOTEDAY.getPermissionString(), 1))
                .executes(new VoteDay())
                .build()
        );
        node.addChild(literal("votesun")
                .requires(Permissions.require(PermissionsHolder.Permission.VOTESUN.getPermissionString(), 1))
                .executes(new VoteSun())
                .build()
        );
        node.addChild(literal("vote")
                .requires(Permissions.require(PermissionsHolder.Permission.VOTE.getPermissionString(), 1))
                .then(argument("type", StringArgumentType.word())
                        .suggests(VoteSuggestion.LIST_SUGGESTION_PROVIDER)
                        .then(argument("vote", BoolArgumentType.bool())
                                .executes(new Vote())
                        )).build()
        );
    }

    public static void registerUtilCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("fly")
                .requires(Permissions.require(PermissionsHolder.Permission.FLY.getPermissionString(), 4))
                .executes(new Fly())
                .build()
        );

        node.addChild(literal("god")
                .requires(Permissions.require(PermissionsHolder.Permission.GOD.getPermissionString(), 4))
                .executes(new God())
                .build()
        );

        node.addChild(literal("heal")
                .requires(Permissions.require(PermissionsHolder.Permission.HEAL.getPermissionString(), 4))
                .executes(new Heal())
                .build()
        );

        node.addChild(literal("feed")
                .requires(Permissions.require(PermissionsHolder.Permission.FEED.getPermissionString(), 4))
                .executes(new Feed())
                .build()
        );

        node.addChild(literal("repair")
                .requires(Permissions.require(PermissionsHolder.Permission.REPAIR.getPermissionString(), 4))
                .executes(new Repair())
                .build()
        );

        node.addChild(literal("nv")
                .requires(Permissions.require(PermissionsHolder.Permission.NIGHTVISION.getPermissionString(), 4))
                .executes(new NightVision())
                .build()
        );

        node.addChild(literal("rtp")
                .requires(Permissions.require(PermissionsHolder.Permission.RTP.getPermissionString(), 4))
                .executes(new RandomTeleport())
                .build()
        );

        node.addChild(literal("tppos")
                .requires(Permissions.require(PermissionsHolder.Permission.TPPOS.getPermissionString(), 4))
                .then(argument("position", Vec3ArgumentType.vec3())
                        .then(argument("world", StringArgumentType.word())
                                .suggests(WorldSuggestion.WORLDS_SUGGESTION_PROVIDER)
                                .executes(new TeleportPosition())
                        )
                ).build()
        );
    }

    public static void registerScreensCommands(RootCommandNode<ServerCommandSource> node) {
        node.addChild(literal("anvil")
                .requires(Permissions.require(PermissionsHolder.Permission.ANVIL.getPermissionString(), 4))
                .executes(new Anvil())
                .build()
        );

        node.addChild(literal("grindstone")
                .requires(Permissions.require(PermissionsHolder.Permission.GRINDSTONE.getPermissionString(), 4))
                .executes(new Grindstone())
                .build()
        );

        node.addChild(literal("workbench")
                .requires(Permissions.require(PermissionsHolder.Permission.WORKBENCH.getPermissionString(), 4))
                .executes(new Workbench())
                .build()
        );

        node.addChild(literal("trashbin")
                .requires(Permissions.require(PermissionsHolder.Permission.TRASHBIN.getPermissionString(), 4))
                .executes(new TrashBin())
                .build()
        );

        node.addChild(literal("enderchest")
                .requires(Permissions.require(PermissionsHolder.Permission.ENDERCHEST.getPermissionString(), 4))
                .executes(new Enderchest())
                .build()
        );
    }
}
