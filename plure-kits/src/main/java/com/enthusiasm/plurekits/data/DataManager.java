package com.enthusiasm.plurekits.data;

import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurekits.PlureKitsEntrypoint;
import com.enthusiasm.plurekits.data.player.PlayerKitData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.UUID;

public class DataManager {
    private final LinkedHashMap<UUID, PlayerKitData> kitDataMap;
    private static DataManager instance = new DataManager();

    public DataManager() {
        this.kitDataMap = new LinkedHashMap<>();
        instance = this;
    }

    public void loadPlayerKitData(ServerPlayerEntity player) {
        PlayerKitData playerKitData = createPlayerKitData(player);

        kitDataMap.put(player.getUuid(), playerKitData);
    }

    public PlayerKitData getPlayerKitData(ServerPlayerEntity player) {
        return kitDataMap.get(player.getUuid());
    }

    public void unloadPlayerKitData(ServerPlayerEntity player) {
        kitDataMap.remove(player.getUuid());
    }

    private PlayerKitData createPlayerKitData(ServerPlayerEntity player) {
        File saveFile = getPlayerKitDataFile(player);
        PlayerKitData playerKitData = new PlayerKitData(player, saveFile);

        if (Files.exists(saveFile.toPath()) && saveFile.length() != 0) {
            try {
                NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(saveFile));
                playerKitData.fromNbt(nbtCompound);
            } catch (Exception e) {
                PlureKitsEntrypoint.LOGGER.warn("Failed to load kits player data for '{}'", player.getName().getString());
            }
        }

        playerKitData.setDirty(true);

        return playerKitData;
    }

    private File getPlayerKitDataFile(ServerPlayerEntity player) {
        Path dataPath = FolderUtils.getDataFolder("kits");
        File playerDataFile;

        playerDataFile = dataPath.resolve(player.getUuidAsString() + ".nbt").toFile();

        try {
            Files.createDirectories(dataPath);
            playerDataFile.createNewFile();
        } catch (Exception ignored) {}

        return playerDataFile;
    }

    public static DataManager getInstance() {
        return instance;
    }
}
