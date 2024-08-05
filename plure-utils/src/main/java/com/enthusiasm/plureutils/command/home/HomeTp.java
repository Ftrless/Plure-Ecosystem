package com.enthusiasm.plureutils.command.home;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.WorldUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.PermissionsHolder;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.data.home.HomeData;
import com.enthusiasm.plureutils.data.home.HomeDataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class HomeTp implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        HomeData homeData = exec(senderPlayer);

        PlayerUtils.sendFeedback(context, "cmd.home.tp.feedback");
        runTp(senderPlayer, homeData);

        return SINGLE_SUCCESS;
    }

    public int runForPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);

        HomeData homeData = exec(targetPlayer);

        PlayerUtils.sendFeedback(context, "cmd.home.tp.target.feedback");
        runTp(senderPlayer, homeData);

        return SINGLE_SUCCESS;
    }

    public HomeData exec(@Nullable ServerPlayerEntity targetPlayer) throws CommandSyntaxException {
        Message playerNotExists = TextUtils.translation("cmd.home.tp.target.error.not-exist", FormatUtils.Colors.ERROR);

        if (targetPlayer == null) {
            throw CommandHelper.createException(playerNotExists);
        }

        HomeDataManager homeDataManager = DataManager.getHomeDataManager();
        HomeData homeData = homeDataManager.getHome(targetPlayer.getUuid());

        Message notExists = TextUtils.translation("cmd.home.tp.error.not-exist", FormatUtils.Colors.ERROR);

        if (homeData == null) {
            throw CommandHelper.createException(notExists);
        }

        if (!homeData.owner.equals(targetPlayer.getUuid())
                && !PermissionsHolder.check(targetPlayer, PermissionsHolder.Permission.BYPASS_TYPE_HOME, 4)) {
            throw CommandHelper.createException(notExists);
        }

        return homeData;
    }

    private void runTp(ServerPlayerEntity targetPlayer, HomeData homeData) {
        PlayerUtils.teleportPlayer(
                targetPlayer,
                homeData.x,
                homeData.y,
                homeData.z,
                homeData.yaw,
                homeData.pitch,
                WorldUtils.getServerWorld(homeData.world, targetPlayer.getServer())
        );
    }
}
