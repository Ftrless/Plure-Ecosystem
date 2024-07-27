package com.enthusiasm.plurekits.util;

import com.enthusiasm.plurekits.data.kit.KitData;
import com.enthusiasm.plurekits.data.kit.KitInventoryData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryUtils {
    public static void giveKit(ServerPlayerEntity player, KitData kit) {
        InventoryUtils.offerAllCopies(kit.getInventory(), player.getInventory());
    }

    public static void offerAllCopies(KitInventoryData source, PlayerInventory target) {
        for (int i = 0; i < source.size(); ++i) {
            target.offerOrDrop(source.getStack(i).copy());
        }
    }
}
