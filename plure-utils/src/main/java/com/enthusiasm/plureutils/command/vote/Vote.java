package com.enthusiasm.plureutils.command.vote;

import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.service.VoteService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class Vote implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String playerName = context.getSource().getPlayerOrThrow().getEntityName();

        String voteType = "vote" + StringArgumentType.getString(context, "type");
        boolean vote = BoolArgumentType.getBool(context, "vote");

        Message noActiveVoting = TextUtils.translation("cmd.vote.error.no_active_voting", FormatUtils.Colors.ERROR);
        Message alreadyVoted = TextUtils.translation("cmd.vote.error.already_voted", FormatUtils.Colors.ERROR);

        if (!VoteService.checkVote(voteType)) {
            throw CommandHelper.createException(noActiveVoting);
        }

        if (VoteService.checkPlayerVote(playerName, voteType)) {
            throw CommandHelper.createException(alreadyVoted);
        }

        VoteService.vote(playerName, voteType, vote);

        PlayerUtils.sendFeedback(
                context,
                "cmd.vote.feedback",
                vote ? "да" : "нет"
        );
    }
}
