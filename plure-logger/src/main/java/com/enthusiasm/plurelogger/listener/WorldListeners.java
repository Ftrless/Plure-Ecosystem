package com.enthusiasm.plurelogger.listener;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actionutils.ActionFactory;
import com.enthusiasm.plurelogger.listener.events.ItemInsertEvent;
import com.enthusiasm.plurelogger.listener.events.ItemRemoveEvent;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;

public class WorldListeners {
    public static void init() {
        ItemInsertEvent.EVENT.register(WorldListeners::onItemInsert);
        ItemRemoveEvent.EVENT.register(WorldListeners::onItemRemove);
    }

    private static void onItemInsert(ItemStack stack, BlockPos pos, World world, String source, ServerPlayerEntity player) {
        if (player != null) {
            ActionQueueService.addToQueue(
                    ActionFactory.itemInsertAction(world, stack, pos, player)
            );
        } else {
            ActionQueueService.addToQueue(
                    ActionFactory.itemInsertAction(world, stack, pos, source)
            );
        }
    }

    private static void onItemRemove(ItemStack stack, BlockPos pos, World world, String source, ServerPlayerEntity player) {
        if (player != null) {
            ActionQueueService.addToQueue(
                    ActionFactory.itemRemoveAction(world, stack, pos, player)
            );
        } else {
            ActionQueueService.addToQueue(
                    ActionFactory.itemRemoveAction(world, stack, pos, source)
            );
        }
    }
}
