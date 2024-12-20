package com.enthusiasm.plurelogger.utils;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public record ItemData(Item item, NbtCompound changes) {}
