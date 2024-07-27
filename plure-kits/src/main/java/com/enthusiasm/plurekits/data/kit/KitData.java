package com.enthusiasm.plurekits.data.kit;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KitData {
    private final KitInventoryData inventory;
    private final long cooldown;
    private final long needsPlayed;
    private @Nullable Item displayItem;

    public KitData(KitInventoryData inventory, long cooldown, long needsPlayed) {
        this.inventory = inventory;
        this.cooldown = cooldown;
        this.needsPlayed = needsPlayed;
    }

    public KitData(KitInventoryData inventory, long cooldown, long needsPlayed, @Nullable Item displayItem) {
        this.inventory = inventory;
        this.cooldown = cooldown;
        this.needsPlayed = needsPlayed;
        this.displayItem = displayItem;
    }

    public KitInventoryData getInventory() {
        return inventory;
    }

    public long getCooldown() {
        return cooldown;
    }

    public long getNeedsPlayed() {
        return needsPlayed;
    }

    public Optional<Item> getDisplayItem() {
        return Optional.ofNullable(displayItem);
    }

    public void setDisplayItem(@Nullable Item item) {
        this.displayItem = item;
    }

    public void writeNBT(NbtCompound root) {
        root.put(NbtKey.INVENTORY, this.getInventory().writeNbt(new NbtList()));
        root.putLong(NbtKey.COOLDOWN, this.getCooldown());
        root.putLong(NbtKey.NEEDS_PLAYED, this.needsPlayed);
        if (this.getDisplayItem().isPresent()) {
            root.putString(
                    NbtKey.DISPLAY_ITEM,
                    Registries.ITEM.getKey(this.getDisplayItem().get()).get().getValue().toString());
        }
    }

    public static KitData fromNBT(NbtCompound kitNbt) {
        var kitInventory = new KitInventoryData();

        kitInventory.readNbt(kitNbt.getList(NbtKey.INVENTORY, NbtElement.COMPOUND_TYPE));

        long cooldown = kitNbt.getLong(NbtKey.COOLDOWN);
        long needsPlayed = kitNbt.getLong(NbtKey.NEEDS_PLAYED);
        var kitDisplayItem = kitNbt.contains(NbtKey.DISPLAY_ITEM)
                ? Registries.ITEM.get(new Identifier(kitNbt.getString(NbtKey.DISPLAY_ITEM)))
                : null;

        return new KitData(kitInventory, cooldown, needsPlayed, kitDisplayItem);
    }

    private static final class NbtKey {
        public static final String INVENTORY = "inventory";
        public static final String COOLDOWN = "cooldown";
        public static final String NEEDS_PLAYED = "needs_played";
        public static final String DISPLAY_ITEM = "display_item";
    }
}
