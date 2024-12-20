package com.enthusiasm.plurelogger.actionutils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actions.*;
import com.enthusiasm.plurelogger.utils.NbtUtils;
import com.enthusiasm.plurelogger.utils.Sources;

public class ActionFactory {
    public static BlockBreakActionType blockBreakAction(World world, BlockPos pos, BlockState state, String source, BlockEntity entity) {
        BlockBreakActionType action = new BlockBreakActionType();
        setBlockData(action, pos, world, Blocks.AIR.getDefaultState(), state, source, entity);
        return action;
    }

    public static BlockChangeActionType blockBreakAction(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity entity) {
        return blockBreakAction(world, pos, state, player, entity, Sources.PLAYER.getSource());
    }

    public static BlockChangeActionType blockBreakAction(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity entity, String source) {
        BlockChangeActionType action = blockBreakAction(world, pos, state, source, entity);
        action.setSourceProfile(player.getGameProfile());
        return action;
    }

    public static BlockChangeActionType blockPlaceAction(World world, BlockPos pos, BlockState state, String source, BlockEntity entity) {
        BlockPlaceActionType action = new BlockPlaceActionType();
        setBlockData(action, pos, world, state, Blocks.AIR.getDefaultState(), source, entity);
        return action;
    }

    public static BlockChangeActionType blockPlaceAction(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity entity) {
        return blockPlaceAction(world, pos, state, player, entity, Sources.PLAYER.getSource());
    }

    public static BlockChangeActionType blockPlaceAction(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity entity, String source) {
        BlockChangeActionType action = blockPlaceAction(world, pos, state, source, entity);
        action.setSourceProfile(player.getGameProfile());
        return action;
    }

    private static void setBlockData(IActionType action, BlockPos pos, World world, BlockState state, BlockState oldState, String source, BlockEntity entity) {
        action.setPos(pos);
        action.setWorld(world.getRegistryKey().getValue());
        action.setObjectIdentifier(Registries.BLOCK.getId(state.getBlock()));
        action.setOldObjectIdentifier(Registries.BLOCK.getId(oldState.getBlock()));
        action.setObjectState(NbtUtils.blockStateToProperties(state).asString());
        action.setOldObjectState(NbtUtils.blockStateToProperties(oldState).asString());
        action.setSourceName(source);
        action.setExtraData(entity != null ? entity.createNbt().asString() : null);
    }

    public static ItemInsertActionType itemInsertAction(World world, ItemStack stack, BlockPos pos, String source) {
        ItemInsertActionType action = new ItemInsertActionType();
        setItemData(action, pos, world, stack, source);
        return action;
    }

    public static ItemInsertActionType itemInsertAction(World world, ItemStack stack, BlockPos pos, PlayerEntity source) {
        ItemInsertActionType action = new ItemInsertActionType();
        setItemData(action, pos, world, stack, Sources.PLAYER.getSource());
        action.setSourceProfile(source.getGameProfile());
        return action;
    }

    public static ItemRemoveActionType itemRemoveAction(World world, ItemStack stack, BlockPos pos, String source) {
        ItemRemoveActionType action = new ItemRemoveActionType();
        setItemData(action, pos, world, stack, source);
        return action;
    }

    public static ItemRemoveActionType itemRemoveAction(World world, ItemStack stack, BlockPos pos, PlayerEntity source) {
        ItemRemoveActionType action = new ItemRemoveActionType();
        setItemData(action, pos, world, stack, Sources.PLAYER.getSource());
        action.setSourceProfile(source.getGameProfile());
        return action;
    }

    public static ItemPickUpActionType itemPickUpAction(ItemEntity entity, PlayerEntity source) {
        ItemPickUpActionType action = new ItemPickUpActionType();
        setItemData(action, entity.getBlockPos(), entity.getWorld(), entity.getStack(), Sources.PLAYER.getSource());
        action.setOldObjectState(entity.writeNbt(new NbtCompound()).asString());
        action.setSourceProfile(source.getGameProfile());
        return action;
    }

    public static ItemDropActionType itemDropAction(ItemEntity entity, PlayerEntity source) {
        ItemDropActionType action = new ItemDropActionType();
        setItemData(action, entity.getBlockPos(), entity.getWorld(), entity.getStack(), Sources.PLAYER.getSource());
        action.setObjectState(entity.writeNbt(new NbtCompound()).asString());
        action.setSourceProfile(source.getGameProfile());
        return action;
    }

    public static IActionType blockChangeAction(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity oldBlockEntity, String source, PlayerEntity player) {
        BlockChangeActionType action = new BlockChangeActionType();
        setBlockData(action, pos, world, newState, oldState, source, oldBlockEntity);
        if (player != null) {
            action.setSourceProfile(player.getGameProfile());
        }
        return action;
    }

    private static void setItemData(IActionType action, BlockPos pos, World world, ItemStack stack, String source) {
        action.setPos(pos);
        action.setWorld(world.getRegistryKey().getValue());
        action.setObjectIdentifier(Registries.ITEM.getId(stack.getItem()));
        action.setSourceName(source);
        action.setExtraData(NbtUtils.itemToProperties(stack).asString());
    }

    public static EntityKillActionType entityKillAction(World world, BlockPos pos, Entity entity, DamageSource cause) {
        Entity killer = cause.getAttacker();
        EntityKillActionType action = new EntityKillActionType();

        if (killer instanceof PlayerEntity) {
            setEntityData(action, pos, world, entity, Sources.PLAYER.getSource());
            action.setSourceProfile(((PlayerEntity) killer).getGameProfile());
        } else if (killer != null) {
            String source = Registries.ENTITY_TYPE.getId(killer.getType()).getPath();
            setEntityData(action, pos, world, entity, source);
        } else {
            setEntityData(action, pos, world, entity, cause.getName());
        }

        return action;
    }

    private static void setEntityData(IActionType action, BlockPos pos, World world, Entity entity, String source) {
        action.setPos(pos);
        action.setWorld(world.getRegistryKey().getValue());
        action.setObjectIdentifier(Registries.ENTITY_TYPE.getId(entity.getType()));
        action.setSourceName(source);
        action.setExtraData(entity.writeNbt(new NbtCompound()).asString());
    }

    public static EntityChangeActionType entityChangeAction(World world, BlockPos pos, NbtCompound oldEntityTags, Entity entity, ItemStack itemStack, Entity entityActor, String sourceType) {
        EntityChangeActionType action = new EntityChangeActionType();

        action.setPos(pos);
        action.setWorld(world.getRegistryKey().getValue());
        action.setObjectIdentifier(Registries.ENTITY_TYPE.getId(entity.getType()));
        action.setOldObjectIdentifier(Registries.ENTITY_TYPE.getId(entity.getType()));

        if (itemStack != null) {
            action.setExtraData(Registries.ITEM.getId(itemStack.getItem()).toString());
        }
        action.setOldObjectState(oldEntityTags.asString());
        action.setObjectState(entity.writeNbt(new NbtCompound()).asString());
        action.setSourceName(sourceType);

        if (entityActor instanceof PlayerEntity) {
            action.setSourceProfile(((PlayerEntity) entityActor).getGameProfile());
        }

        return action;
    }
}
