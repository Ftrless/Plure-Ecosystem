package com.enthusiasm.plureutils.data.home;

import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.data.DataManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.PersistentState;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeDataManager extends PersistentState {
    private File homeDataFile;
    public HashMap<UUID, HomeData> homes;

    public static HomeDataManager onServerStart() {
        HomeDataManager homeDataManager = new HomeDataManager();

        homeDataManager.create();

        return homeDataManager;
    }

    private void create() {
        this.homes = new HashMap<>();

        this.initFile();
    }

    private void initFile() {
        try {
            this.homeDataFile = DataManager.saveDir.resolve("homes.dat").toFile();

            if (!this.homeDataFile.createNewFile() && this.homeDataFile.length() > 0) {
                readFile();
            } else {
                this.markDirty();
                this.save();
            }
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Error while init homes file: " + e.getMessage());
        }
    }

    private void readFile() {
        try {
            this.readNbt(NbtIo.readCompressed(homeDataFile).getCompound("data"));
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Error reading existing homes file: " + e.getMessage());
        }
    }

    private void save() {
        super.save(this.homeDataFile);
    }

    public void addHome(UUID homeName, HomeData homeData) {
        this.homes.put(homeName, homeData);
        this.markDirty();
        this.save();
    }

    public HomeData getHome(UUID homeName) {
        return this.homes.get(homeName);
    }

    public void editHome(UUID homeName, HomeData homeData) {
        this.homes.replace(homeName, homeData);
        this.markDirty();
        this.save();
    }

    public void deleteHome(UUID homeName) {
        this.homes.remove(homeName);
        this.markDirty();
        this.save();
    }

    public HashMap<UUID, HomeData> getHomes() {
        return this.homes;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (Map.Entry<UUID, HomeData> entry : homes.entrySet()) {
            nbt.put(entry.getKey().toString(), entry.getValue().toNBT());
        }

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        for (String key : nbt.getKeys()) {
            homes.put(UUID.fromString(key), HomeData.fromNBT(nbt.getCompound(key)));
        }
    }
}
