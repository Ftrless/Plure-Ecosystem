package com.enthusiasm.plurekits;

import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurekits.data.DataManager;
import com.enthusiasm.plurekits.data.kit.KitData;
import com.enthusiasm.plurekits.event.PlayerEvents;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PlureKitsEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureKits");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureKits");

        KitService.init();
        PlayerEvents.init();
    }
}
