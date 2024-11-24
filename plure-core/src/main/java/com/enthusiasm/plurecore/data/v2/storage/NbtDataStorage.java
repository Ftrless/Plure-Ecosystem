package com.enthusiasm.plurecore.data.v2.storage;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

public class NbtDataStorage implements IDataStorage<NbtCompound>{
    @Override
    public boolean save(MinecraftServer server, UUID player, NbtCompound settings) {
        return false;
    }

    @Override
    public @Nullable NbtCompound load(MinecraftServer server, UUID player) {
        return null;
    }
}
