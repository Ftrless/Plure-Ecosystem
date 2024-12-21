package com.enthusiasm.plurelogger.storage.nbt.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public record DeathEntity(Instant date, boolean isSaved, int exp,
                          DeathEntity.Location location, String reason,
                          List<ItemStack> inventory, List<ItemStack> armor, List<ItemStack> curios) {

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putString("date", date.toString());
        tag.putBoolean("isSaved", isSaved);
        tag.putInt("exp", exp);

        NbtCompound locationTag = new NbtCompound();
        locationTag.putInt("x", location.x());
        locationTag.putInt("y", location.y());
        locationTag.putInt("z", location.z());
        locationTag.putString("dimension", location.dimension());

        tag.put("location", locationTag);

        tag.putString("reason", reason);

        tag.put("inventory", toItemStackNbtList(inventory));
        tag.put("armor", toItemStackNbtList(armor));
        tag.put("curios", toItemStackNbtList(curios));

        return tag;
    }

    public static DeathEntity fromNbt(NbtCompound tag) {
        Instant date = Instant.parse(tag.getString("date"));
        boolean isSaved = tag.getBoolean("isSaved");
        int exp = tag.getInt("exp");

        NbtCompound locationTag = tag.getCompound("location");
        Location location = new Location(
                locationTag.getInt("x"),
                locationTag.getInt("y"),
                locationTag.getInt("z"),
                locationTag.getString("dimension")
        );

        String reason = tag.getString("reason");

        List<ItemStack> inventory = fromItemStackNbtList(tag.getList("inventory", NbtElement.COMPOUND_TYPE));
        List<ItemStack> armor = fromItemStackNbtList(tag.getList("armor", NbtElement.COMPOUND_TYPE));
        List<ItemStack> curios = fromItemStackNbtList(tag.getList("curios", NbtElement.COMPOUND_TYPE));

        return new DeathEntity(date, isSaved, exp, location, reason, inventory, armor, curios);
    }

    private NbtList toItemStackNbtList(List<ItemStack> itemStacks) {
        NbtList listTag = new NbtList();
        for (ItemStack stack : itemStacks) {
            NbtCompound stackTag = new NbtCompound();
            stack.writeNbt(stackTag);
            listTag.add(stackTag);
        }
        return listTag;
    }

    private static List<ItemStack> fromItemStackNbtList(NbtList listTag) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (NbtElement tag : listTag) {
            if (tag instanceof NbtCompound compound) {
                ItemStack stack = ItemStack.fromNbt(compound);
                itemStacks.add(stack);
            }
        }
        return itemStacks;
    }

    public record Location(int x, int y, int z, String dimension) {}
}
