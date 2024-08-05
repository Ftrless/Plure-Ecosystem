package com.enthusiasm.plureutils.command.util.screens;

import com.enthusiasm.plurecore.utils.text.FormatUtils;
import org.jetbrains.annotations.NotNull;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TrashBin extends SimpleScreen {
    private static final ScreenHandlerFactory SCREEN_HANDLER_FACTORY = (syncId, inventory, player) ->
            GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, new SimpleInventory(27));

    @Override
    protected Text getScreenTitle() {
        return Text.translatable("cmd.trashbin.name", FormatUtils.Colors.DEFAULT);
    }

    @Override
    protected @NotNull ScreenHandlerFactory getScreenHandlerFactory() {
        return SCREEN_HANDLER_FACTORY;
    }

    @Override
    protected void onOpen(ServerPlayerEntity player) {}
}
