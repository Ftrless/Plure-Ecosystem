package com.enthusiasm.plurelogger.registry;

import com.enthusiasm.plurelogger.PlureLoggerEntrypoint;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;

public final class WorldRegistry {
    public static void registerWorlds() {
        PlureLoggerEntrypoint.SERVER.getWorldRegistryKeys()
                .forEach(worldRegistryKey -> DatabaseService.registerWorld(worldRegistryKey.getValue().toString()));
    }
}
