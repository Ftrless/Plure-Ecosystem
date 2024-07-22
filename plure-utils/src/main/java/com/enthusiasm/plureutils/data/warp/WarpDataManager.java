package com.enthusiasm.plureutils.data.warp;

import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.data.DataManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.PersistentState;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpDataManager extends PersistentState {
    private File warpDataFile;
    public HashMap<String, WarpData> warps;

    public static WarpDataManager onServerStart() {
        WarpDataManager warpDataManager = new WarpDataManager();

        warpDataManager.create();

        return warpDataManager;
    }

    private void create() {
        this.warps = new HashMap<>();

        this.initFile();
    }

    private void initFile() {
        try {
            this.warpDataFile = DataManager.saveDir.resolve("warps.dat").toFile();

            if (!this.warpDataFile.createNewFile() && this.warpDataFile.length() > 0) {
                readFile();
            } else {
                this.markDirty();
                this.save();
            }
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Error while init warps file: {}", e.getMessage());
        }
    }

    private void readFile() {
        try {
            this.readNbt(NbtIo.readCompressed(warpDataFile).getCompound("data"));
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Error reading existing warps file: {}", e.getMessage());
        }
    }

    private void save() {
        super.save(this.warpDataFile);
    }

    public void addWarp(String warpName, WarpData warpData) {
        this.warps.put(warpName, warpData);
        this.markDirty();
        this.save();
    }

    public WarpData getWarp(String warpName) {
        return this.warps.get(warpName);
    }

    public void editWarp(String warpName, WarpData warpData) {
        this.warps.replace(warpName, warpData);
        this.markDirty();
        this.save();
    }

    public void deleteWarp(String warpName) {
        this.warps.remove(warpName);
        this.markDirty();
        this.save();
    }

    public void invitePlayer(String warpName, UUID invitedPlayerUUID) {
        WarpData warpData = this.warps.get(warpName);

        if (warpData != null) {
            warpData.invited.add(invitedPlayerUUID);
            this.markDirty();
            this.save();
        }
    }
    public void deInvitePlayer(String warpName, UUID invitedPlayerUUID) {
        WarpData warpData = this.warps.get(warpName);

        if (warpData != null) {
            warpData.invited.remove(invitedPlayerUUID);
            this.markDirty();
            this.save();
        }
    }

    public List<Map.Entry<String, WarpData>> globalWarps() {
        return this.listWarps()
                .entrySet()
                .stream()
                .filter(warp -> warp.getValue().global)
                .toList();
    }

    public List<Map.Entry<String, WarpData>> playerWarps(UUID playerUUID) {
        return this.listWarps()
                .entrySet()
                .stream()
                .filter(warp -> warp.getValue().owner.equals(playerUUID))
                .toList();
    }

    public List<Map.Entry<String, WarpData>> invitedWarps(UUID playerUUID) {
        return this.listWarps()
                .entrySet()
                .stream()
                .filter(warp -> warp.getValue().invited.contains(playerUUID))
                .toList();
    }

    public HashMap<String, WarpData> listWarps() {
        return this.warps;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (Map.Entry<String, WarpData> entry : warps.entrySet()) {
            nbt.put(entry.getKey(), entry.getValue().toNBT());
        }

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        for (String key : nbt.getKeys()) {
            warps.put(key, WarpData.fromNBT(nbt.getCompound(key)));
        }
    }
}

