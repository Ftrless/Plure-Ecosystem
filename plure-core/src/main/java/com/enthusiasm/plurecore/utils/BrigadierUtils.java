package com.enthusiasm.plurecore.utils;

import java.util.Locale;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class BrigadierUtils {
    public static <S> LiteralCommandNode<S> buildRedirect(String alias, LiteralCommandNode<S> destination) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder
                .<S>literal(alias.toLowerCase(Locale.ROOT))
                .requires(destination.getRequirement())
                .forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());

        for (CommandNode<S> child : destination.getChildren()) {
            builder.then(child);
        }

        return builder.build();
    }
}
