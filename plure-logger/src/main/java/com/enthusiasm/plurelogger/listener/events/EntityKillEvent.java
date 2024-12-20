package com.enthusiasm.plurelogger.listener.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EntityKillEvent {
    void onEntityKill(World world, BlockPos pos, Entity entity, DamageSource source);

    Event<EntityKillEvent> EVENT = EventFactory.createArrayBacked(EntityKillEvent.class,
            (listeners) -> (world, pos, entity, source) -> {
                for (EntityKillEvent listener : listeners) {
                    listener.onEntityKill(world, pos, entity, source);
                }
            }
    );

    static void invoke(World world, BlockPos pos, Entity entity, DamageSource source) {
        EVENT.invoker().onEntityKill(world, pos, entity, source);
    }
}
