package com.enthusiasm.plureutils.command.util.screens;

import com.enthusiasm.plureutils.command.util.screens.handler.WorkbenchHandler;
import org.jetbrains.annotations.NotNull;

import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class Workbench extends SimpleScreen {
    private static final ScreenHandlerFactory SCREEN_HANDLER_FACTORY = (syncId, inventory, player) ->
            new WorkbenchHandler(
                    syncId,
                    inventory,
                    ScreenHandlerContext.create(player.getEntityWorld(), player.getBlockPos())
            );

    @Override
    protected Text getScreenTitle() {
        return Text.translatable("block.minecraft.crafting_table");
    }

    @Override
    protected @NotNull ScreenHandlerFactory getScreenHandlerFactory() {
        return SCREEN_HANDLER_FACTORY;
    }

    @Override
    protected void onOpen(ServerPlayerEntity player) {
        player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
    }
}