package com.enthusiasm.plurekits;

import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurekits.data.DataManager;
import com.enthusiasm.plurekits.data.kit.KitData;
import com.enthusiasm.plurekits.data.player.PlayerKitData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class KitService {
    private static final Map<String, KitData> KITS = new HashMap<>();
    private static final File kitsDir = FolderUtils.getConfigFolder().resolve("plure-kits").toFile();

    public static void init() {
        if (!kitsDir.mkdirs()) {
            loadKits();
        }
    }

    public static void loadKits() {
        KITS.clear();
        File[] kitFiles = kitsDir.listFiles();

        if (kitFiles == null) {
            return;
        }

        Arrays.stream(kitFiles).forEach(kitFile -> {
            try {
                String fileName = kitFile.getName();
                NbtCompound kitNbt = NbtIo.read(kitFile);
                String kitName = fileName.substring(0, fileName.length() - 4);

                PlureKitsEntrypoint.LOGGER.info("Загрузка кита - {}", kitName);
                KITS.put(kitName, KitData.fromNBT(kitNbt));
            } catch (Exception e) {
                PlureKitsEntrypoint.LOGGER.error("Ошибка при загрузке кита - {}", e.getMessage());
            }
        });
    }

    public static Stream<Map.Entry<String, KitData>> getAllKitsForPlayer(ServerPlayerEntity player) {
        return KITS.entrySet()
                .stream()
                .filter(kitEntry ->
                        PermissionHolder.check(player, PermissionHolder.getKitPermission(kitEntry.getKey()), 4)
                );
    }

    public static Stream<Map.Entry<String, KitData>> getClaimableKitsForPlayer(ServerPlayerEntity player) {
        PlayerKitData playerData = DataManager.getInstance().getPlayerKitData(player);
        long currentTime = Util.getEpochTimeMs();

        return getAllKitsForPlayer(player)
                .filter(entry -> (playerData.getKitUsedTime(entry.getKey()) + entry.getValue().getCooldown())
                        -
                        currentTime <= 0
                );
    }

    @Nullable
    public static KitData getKit(String kitName) {
        return KITS.getOrDefault(kitName, null);
    }

    public static void removeKit(String kitName) {
        KITS.remove(kitName);

        try {
            Files.delete(getKitsDir().toPath().resolve(String.format("%s.nbt", kitName)));
        } catch (Exception ignored) {}
    }

    public static File getKitsDir() {
        return kitsDir;
    }
}
