package com.enthusiasm.plurelogger.command;

import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;

public interface ICommand {
    LiteralCommandNode<ServerCommandSource> build();
}
