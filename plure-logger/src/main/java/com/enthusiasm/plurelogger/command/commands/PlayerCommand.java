package com.enthusiasm.plurelogger.command.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.command.ICommand;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.MessageUtils;
import com.enthusiasm.plurelogger.utils.PlayerResult;

public class PlayerCommand implements ICommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("player")
                .then(
                        argument("player", GameProfileArgumentType.gameProfile())
                                .executes(context -> lookupPlayer(GameProfileArgumentType.getProfileArgument(context, "player"), context.getSource()))
                )
                .build();
    }

    private int lookupPlayer(Collection<GameProfile> profiles, ServerCommandSource source) {
        ThreadUtils.runAsync(() -> {
            Set<GameProfile> profileSet = Set.copyOf(profiles);
            List<PlayerResult> players = DatabaseService.selectPlayers(profileSet);
            MessageUtils.sendPlayerMessage(source, players);
        });

        return 1;
    }
}
