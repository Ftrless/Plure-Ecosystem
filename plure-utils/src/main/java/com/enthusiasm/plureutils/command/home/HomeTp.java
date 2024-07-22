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

public class HomeTp implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();

        exec(context, senderPlayer);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
        HomeDataManager homeDataManager = DataManager.getHomeDataManager();
        HomeData homeData = homeDataManager.getHome(senderPlayer.getUuid());

        Message notExists = TextUtils.translation("cmd.home.tp.error.not-exist", FormatUtils.Colors.ERROR);

        if (homeData == null) {
            throw CommandHelper.createException(notExists);
        }

        if (!homeData.owner.equals(senderPlayer.getUuid())
                && !PermissionsHolder.check(senderPlayer, PermissionsHolder.Permission.BYPASS_TYPE_HOME, 4)) {
            throw CommandHelper.createException(notExists);
        }

        PlayerUtils.teleportPlayer(
                senderPlayer,
                homeData.x,
                homeData.y,
                homeData.z,
                homeData.yaw,
                homeData.pitch,
                WorldUtils.getServerWorld(homeData.world, context.getSource().getServer())
        );

        PlayerUtils.sendFeedback(context, "cmd.home.tp.feedback");
    }
}
