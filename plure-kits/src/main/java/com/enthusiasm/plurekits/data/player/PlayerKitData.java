package com.enthusiasm.plurekits.data.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlayerKitData extends PlayerData {
    private final Map<String, Long> kitUsedTimes;

    public PlayerKitData(ServerPlayerEntity player, File saveFile) {
        super(player, saveFile);
        kitUsedTimes = new HashMap<>();
    }

    public void useKit(String kitName) {
        kitUsedTimes.put(kitName, Util.getEpochTimeMs());
        setDirty(true);
        save();
    }

    public long getKitUsedTime(String kitName) {
        try {
            return kitUsedTimes.get(kitName);
        } catch (NullPointerException notYetUsed) {
            return 0;
        }
    }

    @Override
    public @NotNull NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound kitUsedTimesNbt = new NbtCompound();
        kitUsedTimes.forEach(kitUsedTimesNbt::putLong);

        nbt.put("kitUsedTimes", kitUsedTimesNbt);

        return nbt;
    }

    @Override
    public void fromNbt(NbtCompound nbtCompound) {
        NbtCompound dataTag = nbtCompound.getCompound("data");
        NbtCompound kitUsedTimesNbt = dataTag.getCompound("kitUsedTimes");

        for (String key : kitUsedTimesNbt.getKeys()) {
            this.kitUsedTimes.put(key, kitUsedTimesNbt.getLong(key));
        }
    }

    public void resetKitCooldown(String kitName) {
        this.kitUsedTimes.remove(kitName);
        this.setDirty(true);
    }

    public void resetAllKits() {
        this.kitUsedTimes.clear();
        this.setDirty(true);
    }
}
