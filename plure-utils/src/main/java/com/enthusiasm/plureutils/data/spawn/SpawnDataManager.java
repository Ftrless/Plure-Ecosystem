package com.enthusiasm.plureutils.data.spawn;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.data.DataManager;

public class SpawnDataManager extends PersistentState {
    private File spawnDataFile;
    private SpawnData spawnData = null;

    public static SpawnDataManager onServerStart() {
        SpawnDataManager spawnDataManager = new SpawnDataManager();

        spawnDataManager.initFile();

        return spawnDataManager;
    }

    public void setSpawn(ServerPlayerEntity senderPlayer) {
        this.spawnData = new SpawnData(
                senderPlayer.getWorld().getRegistryKey().toString(),
                senderPlayer.getX(),
                senderPlayer.getY(),
                senderPlayer.getZ(),
                senderPlayer.getYaw(),
                senderPlayer.getPitch()
        );

        this.save();
    }

    @Nullable
    public SpawnData getSpawn() {
        return this.spawnData;
    }

    private void initFile() {
        try {
            this.spawnDataFile = DataManager.saveDir.resolve("spawn.dat").toFile();

            if (!this.spawnDataFile.createNewFile() && this.spawnDataFile.length() > 0) {
                readFile();
                return;
            }

            this.save();
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Error while init spawn data file: {}", e.getMessage());
        }
    }

    private void readFile() {
        try {
            this.readNbt(NbtIo.readCompressed(this.spawnDataFile).getCompound("data"));
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Error reading existing spawn data file: {}", e.getMessage());
        }
    }

    private void save() {
        this.markDirty();
        super.save(this.spawnDataFile);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (this.spawnData == null) return nbt;

        nbt.put("spawnData", spawnData.toNBT());

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        this.spawnData = SpawnData.fromNBT(nbt.getCompound("spawnData"));
    }
}
