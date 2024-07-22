package com.enthusiasm.plureutils.command.vote;

import com.enthusiasm.plurecore.utils.CooldownUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.config.ConfigManager;
import com.enthusiasm.plureutils.service.VoteService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class VoteSun implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getServer().getOverworld();
        CooldownUtils.getRemainingTime(CooldownUtils.getGlobalCooldown("votesun"), ConfigManager.getConfig().globalCooldown);

        Message alreadySun = TextUtils.translation("cmd.votesun.error.already_sun", FormatUtils.Colors.ERROR);
        Message alreadyVoting = TextUtils.translation("cmd.vote.error.already_voting", FormatUtils.Colors.ERROR);
        Message cooldown = TextUtils.translation("cooldown.global.feedback", FormatUtils.Colors.ERROR);

        if (!CooldownUtils.isGlobalCooldownExpired("votesun", ConfigManager.getConfig().globalCooldown)) {
            throw CommandHelper.createException(cooldown);
        }

        if (!world.isRaining() && !world.isThundering()) {
            throw CommandHelper.createException(alreadySun);
        }

        if (VoteService.checkVote("votesun")) {
            throw CommandHelper.createException(alreadyVoting);
        }

        VoteService.startVote("votesun", context.getSource().getServer());
        VoteService.vote(context.getSource().getPlayerOrThrow().getEntityName(), "votesun", true);

        CooldownUtils.addGlobalCooldown("votesun");
    }
}
