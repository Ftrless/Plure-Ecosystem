package com.enthusiasm.plurelogger.actions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurelogger.actionutils.Preview;

public class ItemInsertActionType extends ItemChangeActionType {
    private final String identifier = "item-insert";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void previewRollback(Preview preview, ServerPlayerEntity player) {
        previewItemChange(preview, player, false);
    }

    @Override
    public void previewRestore(Preview preview, ServerPlayerEntity player) {
        previewItemChange(preview, player, true);
    }

    @Override
    public boolean rollback(MinecraftServer server) {
        return removeMatchingItem(server);
    }

    @Override
    public boolean restore(MinecraftServer server) {
        return addItem(server);
    }
}
