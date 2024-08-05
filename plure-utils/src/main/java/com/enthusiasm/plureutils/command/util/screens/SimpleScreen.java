package com.enthusiasm.plureutils.command.util.screens;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleScreen implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var senderPlayer = context.getSource().getPlayerOrThrow();

        senderPlayer.openHandledScreen(createNamedScreenHandlerFactory());
        onOpen(senderPlayer);

        return SINGLE_SUCCESS;
    }

    protected NamedScreenHandlerFactory createNamedScreenHandlerFactory() {
        return new SimpleNamedScreenHandlerFactory(getScreenHandlerFactory(), getScreenTitle());
    }

    protected abstract Text getScreenTitle();
    protected abstract @NotNull ScreenHandlerFactory getScreenHandlerFactory();
    protected abstract void onOpen(ServerPlayerEntity player);
}
