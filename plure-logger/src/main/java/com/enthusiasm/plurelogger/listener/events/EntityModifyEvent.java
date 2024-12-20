package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EntityModifyEvent {
    void onEntityModify(World world, BlockPos pos, NbtCompound oldEntityTags, Entity newEntity, ItemStack itemStack, Entity entityActor, String sourceType);

    Event<EntityModifyEvent> EVENT = EventFactory.createArrayBacked(EntityModifyEvent.class,
            (listeners) -> (world, pos, oldEntityTags, newEntity, itemStack, entityActor, sourceType) -> {
                for (EntityModifyEvent listener : listeners) {
                    listener.onEntityModify(world, pos, oldEntityTags, newEntity, itemStack, entityActor, sourceType);
                }
            }
    );

    static void invoke(World world, BlockPos pos, NbtCompound oldEntityTags, Entity newEntity, ItemStack itemStack, Entity entityActor, String sourceType) {
        EVENT.invoker().onEntityModify(world, pos, oldEntityTags, newEntity, itemStack, entityActor, sourceType);
    }
}
