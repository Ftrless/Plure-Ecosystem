package com.enthusiasm.plurecore.command;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistryWrapper {
    public static LiteralCommandNode<ServerCommandSource> buildNode(CommandNode<ServerCommandSource> rootNode, String nodeName) {
        LiteralCommandNode<ServerCommandSource> childNode = literal(nodeName).build();
        rootNode.addChild(childNode);

        return childNode;
    }
}
