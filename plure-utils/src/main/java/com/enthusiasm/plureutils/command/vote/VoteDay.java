package com.enthusiasm.plureutils.command.vote;

import com.enthusiasm.plurecore.utils.CooldownUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.config.ConfigManager;
import com.enthusiasm.plureutils.service.VoteService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class VoteDay implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getServer().getOverworld();
        String formattedRemainingTime = CooldownUtils.getRemainingTime(CooldownUtils.getGlobalCooldown("voteday"), ConfigManager.getConfig().globalCooldown);

        Message alreadyDaytime = TextUtils.translation("cmd.voteday.error.already_daytime", FormatUtils.Colors.ERROR);
        Message alreadyVoting = TextUtils.translation("cmd.vote.error.already_voting", FormatUtils.Colors.ERROR);
        Message cooldown = TextUtils.translation("cooldown.global.feedback", FormatUtils.Colors.ERROR, formattedRemainingTime);

        if (!CooldownUtils.isGlobalCooldownExpired("voteday", ConfigManager.getConfig().globalCooldown)) {
            throw CommandHelper.createException(cooldown);
        }

        if (world.isDay()) {
            throw CommandHelper.createException(alreadyDaytime);
        }

        if (VoteService.checkVote("voteday")) {
            throw CommandHelper.createException(alreadyVoting);
        }

        VoteService.startVote("voteday", context.getSource().getServer());
        VoteService.vote(context.getSource().getPlayerOrThrow().getEntityName(), "voteday", true);

        CooldownUtils.addGlobalCooldown("voteday");
    }
}
