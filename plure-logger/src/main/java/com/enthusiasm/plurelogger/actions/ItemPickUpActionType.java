package com.enthusiasm.plurelogger.actions;

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

public class ItemPickUpActionType extends AbstractActionType {
    private final String identifier = "item-pick-up";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getTranslationType() {
        var item = Registries.ITEM.get(getObjectIdentifier());
        if (item instanceof BlockItem && !(item instanceof AliasedBlockItem)) {
            return "block";
        } else {
            return "item";
        }
    }

    private ItemStack getStack(MinecraftServer server) {
        return NbtUtils.itemFromProperties(getExtraData(), getObjectIdentifier(), server.getRegistryManager());
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        ItemStack stack = getStack(source.getServer());

        return Text.literal(stack.getCount() + " ")
                .append(Text.translatable(Util.createTranslationKey(getTranslationType(), getObjectIdentifier())))
                .setStyle(TextColorPallet.getSecondaryVariant())
                .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack))));
    }

    @SneakyThrows
    @Override
    public boolean rollback(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        NbtCompound oldEntity = StringNbtReader.parse(getOldObjectState());
        var uuid = oldEntity.getUuid(NbtUtils.UUID);

        Entity entity = world != null ? world.getEntity(uuid) : null;

        if (entity == null) {
            entity = new ItemEntity(EntityType.ITEM, world);
            entity.readNbt(oldEntity);
            if (world != null) {
                world.spawnEntity(entity);
            }
        }
        return true;
    }

    @SneakyThrows
    @Override
    public boolean restore(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        NbtCompound oldEntity = StringNbtReader.parse(getOldObjectState());
        var uuid = oldEntity.getUuid(NbtUtils.UUID);

        Entity entity = world != null ? world.getEntity(uuid) : null;

        if (entity != null) {
            entity.remove(Entity.RemovalReason.DISCARDED);
            return true;
        }
        return false;
    }
}
