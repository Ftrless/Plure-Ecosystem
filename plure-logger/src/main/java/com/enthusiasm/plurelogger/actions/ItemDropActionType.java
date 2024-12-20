package com.enthusiasm.plurelogger.actions;

import java.util.UUID;

import lombok.SneakyThrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
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
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import com.enthusiasm.plurelogger.utils.NbtUtils;
import com.enthusiasm.plurelogger.utils.TextColorPallet;
import com.enthusiasm.plurelogger.utils.WorldUtils;

public class ItemDropActionType extends AbstractActionType {
    private final String identifier = "item-drop";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getTranslationType() {
        var item = Registries.ITEM.get(getObjectIdentifier());
        return (item instanceof BlockItem && !(item instanceof AliasedBlockItem)) ? "block" : "item";
    }

    private ItemStack getStack(MinecraftServer server) {
        return NbtUtils.itemFromProperties(getExtraData(), getObjectIdentifier(), server.getRegistryManager());
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        ItemStack stack = getStack(source.getServer());

        return Text.literal(stack.getCount() + " ")
                .append(Text.translatable(
                        Util.createTranslationKey(getTranslationType(), getObjectIdentifier())
                ))
                .setStyle(TextColorPallet.getSecondaryVariant())
                .styled(style -> style.withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_ITEM,
                        new HoverEvent.ItemStackContent(stack)
                )));
    }

    @SneakyThrows
    @Override
    public boolean rollback(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());
        NbtCompound newEntity = StringNbtReader.parse(getObjectState());

        UUID uuid = newEntity.getUuid(NbtUtils.UUID);
        if (uuid == null) return false;

        Entity entity = world != null ? world.getEntity(uuid) : null;
        if (entity != null) {
            entity.remove(Entity.RemovalReason.DISCARDED);
            return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public boolean restore(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());
        NbtCompound newEntity = StringNbtReader.parse(getObjectState());

        UUID uuid = newEntity.getUuid(NbtUtils.UUID);
        if (uuid == null) return false;

        Entity entity = world != null ? world.getEntity(uuid) : null;
        if (entity == null && world != null) {
            ItemEntity itemEntity = new ItemEntity(EntityType.ITEM, world);
            itemEntity.readNbt(newEntity);
            world.spawnEntity(itemEntity);
        }
        return true;
    }
}
