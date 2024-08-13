package com.enthusiasm.plureutils.command.util.screens.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

public class AnvilHandler extends AnvilScreenHandler {
    public AnvilHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}