package com.enthusiasm.plurelogger.actions;

import java.util.UUID;

import lombok.SneakyThrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.enthusiasm.plurelogger.utils.NbtUtils;
import com.enthusiasm.plurelogger.utils.TextColorPallet;
import com.enthusiasm.plurelogger.utils.WorldUtils;

public class EntityChangeActionType extends AbstractActionType {
    private final String identifier = "entity-change";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getTranslationType() {
        Identifier itemId = Identifier.tryParse(getExtraData());
        if (itemId != null) {
            if (Registries.ITEM.get(itemId) instanceof BlockItem && !(Registries.ITEM.get(itemId) instanceof AliasedBlockItem)) {
                return "block";
            } else {
                return "item";
            }
        }
        return "entity";
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        MutableText text = Text.literal("");
        text.append(Text.translatable(Util.createTranslationKey("entity", getObjectIdentifier()))
                .setStyle(TextColorPallet.getSecondary())
                .styled(style -> style.withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal(getObjectIdentifier().toString())
                ))));

        if (getExtraData() != null && !Identifier.tryParse("minecraft:air").equals(Identifier.tryParse(getExtraData()))) {
            Identifier itemId = Identifier.tryParse(getExtraData());
            if (itemId != null) {
                ItemStack stack = new ItemStack(Registries.ITEM.get(itemId));
                text.append(Text.literal(" ")
                                .append(Text.translatable("action_message.with"))
                                .append(" "))
                        .append(Text.translatable(Util.createTranslationKey(getTranslationType(), itemId))
                                .setStyle(TextColorPallet.getSecondary())
                                .styled(style -> style.withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_ITEM,
                                        new HoverEvent.ItemStackContent(stack)
                                ))));
            }
        }
        return text;
    }

    @SneakyThrows
    @Override
    public boolean rollback(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        if (world != null) {
            NbtCompound oldEntityData = StringNbtReader.parse(getOldObjectState());
            UUID entityUUID = oldEntityData.getUuid(NbtUtils.UUID);

            if (entityUUID != null) {
                Entity entity = world.getEntity(entityUUID);
                if (entity != null) {
                    if (entity instanceof ItemFrameEntity) {
                        ((ItemFrameEntity) entity).setHeldItemStack(ItemStack.EMPTY);
                    }
                    if (entity instanceof LivingEntity || entity instanceof AbstractDecorationEntity) {
                        entity.readNbt(oldEntityData);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @SneakyThrows
    @Override
    public boolean restore(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        if (world != null) {
            NbtCompound newEntityData = StringNbtReader.parse(getObjectState());
            UUID entityUUID = newEntityData.getUuid(NbtUtils.UUID);

            if (entityUUID != null) {
                Entity entity = world.getEntity(entityUUID);
                if (entity != null) {
                    if (entity instanceof ItemFrameEntity) {
                        ((ItemFrameEntity) entity).setHeldItemStack(ItemStack.EMPTY);
                    }
                    if (entity instanceof LivingEntity || entity instanceof AbstractDecorationEntity) {
                        entity.readNbt(newEntityData);
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
