package com.enthusiasm.plurecore;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.data.example.DataManager;
import com.enthusiasm.plurecore.event.PlayerEvents;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureCoreEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureCore");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureCore");

        CacheService.init();
        PlayerEvents.init();
        DataManager.init();
    }
}
