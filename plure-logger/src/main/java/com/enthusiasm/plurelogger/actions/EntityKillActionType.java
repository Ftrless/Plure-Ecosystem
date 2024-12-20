package com.enthusiasm.plurelogger.actions;

import java.util.Optional;

import lombok.SneakyThrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.utils.NbtUtils;
import com.enthusiasm.plurelogger.utils.WorldUtils;

public class EntityKillActionType extends AbstractActionType {
    private final String identifier = "entity-kill";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getTranslationType() {
        return "entity";
    }

    @SneakyThrows
    @Override
    public void previewRollback(Preview preview, ServerPlayerEntity player) {
        ServerWorld world = WorldUtils.getWorld(player.getServer(), getWorld());

        Optional<EntityType<?>> entityTypeOpt = Registries.ENTITY_TYPE.getOrEmpty(getObjectIdentifier());
        if (entityTypeOpt.isEmpty()) return;

        EntityType<?> entityType = entityTypeOpt.get();
        LivingEntity entity = (LivingEntity) entityType.create(world);
        if (entity == null) return;

        NbtCompound nbtData = StringNbtReader.parse(getExtraData());
        entity.readNbt(nbtData);
        entity.setHealth(entity.getMaxHealth());
        entity.setVelocity(Vec3d.ZERO);
        entity.setFireTicks(0);

        EntityTrackerEntry entityTrackerEntry = new EntityTrackerEntry(world, entity, 1, false, (f) -> {});
        entityTrackerEntry.startTracking(player);
        preview.getSpawnedEntityTrackers().add(entityTrackerEntry);
    }

    @SneakyThrows
    @Override
    public void previewRestore(Preview preview, ServerPlayerEntity player) {
        ServerWorld world = WorldUtils.getWorld(player.getServer(), getWorld());

        NbtCompound tag = StringNbtReader.parse(getExtraData());
        if (tag.containsUuid("UUID")) {
            java.util.UUID uuid = tag.getUuid("UUID");
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                EntityTrackerEntry entityTrackerEntry = new EntityTrackerEntry(world, entity, 1, false, (f) -> {});
                entityTrackerEntry.stopTracking(player);
                preview.getRemovedEntityTrackers().add(entityTrackerEntry);
            }
        }
    }

    @SneakyThrows
    @Override
    public boolean rollback(MinecraftServer server) {
        World world = WorldUtils.getWorld(server, getWorld());

        Optional<EntityType<?>> entityTypeOpt = Registries.ENTITY_TYPE.getOrEmpty(getObjectIdentifier());
        if (entityTypeOpt.isPresent()) {
            EntityType<?> entityType = entityTypeOpt.get();
            Entity entity = entityType.create(world);
            if (entity != null) {
                NbtCompound nbtData = StringNbtReader.parse(getExtraData());
                entity.readNbt(nbtData);
                entity.setVelocity(Vec3d.ZERO);
                entity.setFireTicks(0);
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).setHealth(((LivingEntity) entity).getMaxHealth());
                }
                world.spawnEntity(entity);
                return true;
            }
        }

        return false;
    }

    @SneakyThrows
    @Override
    public boolean restore(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        java.util.UUID uuid = StringNbtReader.parse(getExtraData()).getUuid(NbtUtils.UUID);
        if (uuid != null) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                return true;
            }
        }

        return false;
    }
}
