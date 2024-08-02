package com.enthusiasm.plurekits.data;

import com.enthusiasm.plurekits.data.player.PlayerKitData;
import net.minecraft.server.network.ServerPlayerEntity;

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
        PlayerKitData playerKitData = new PlayerKitData(player);
        kitDataMap.put(player.getUuid(), playerKitData);
    }

    public PlayerKitData getPlayerKitData(ServerPlayerEntity player) {
        return kitDataMap.get(player.getUuid());
    }

    public void unloadPlayerKitData(ServerPlayerEntity player) {
        kitDataMap.remove(player.getUuid());
    }

    public static DataManager getInstance() {
        return instance;
    }
}
