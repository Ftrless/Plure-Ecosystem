package com.enthusiasm.plurelogger.listener;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actionutils.ActionFactory;
import com.enthusiasm.plurelogger.listener.events.EntityKillEvent;
import com.enthusiasm.plurelogger.listener.events.EntityModifyEvent;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;

public class EntityListeners {
    public static void init() {
        EntityKillEvent.EVENT.register(EntityListeners::onKill);
        EntityModifyEvent.EVENT.register(EntityListeners::onModify);
    }

    private static void onKill(World world, BlockPos pos, Entity entity, DamageSource source) {
        ActionQueueService.addToQueue(
                ActionFactory.entityKillAction(world, pos, entity, source)
        );
    }

    private static void onModify(World world, BlockPos pos, NbtCompound oldEntityTags, Entity entity, ItemStack itemStack, Entity entityActor, String sourceType) {
        ActionQueueService.addToQueue(
                ActionFactory.entityChangeAction(world, pos, oldEntityTags, entity, itemStack, entityActor, sourceType)
        );
    }
}
