package com.enthusiasm.plureutils;

import com.enthusiasm.plureutils.config.ConfigManager;
import com.enthusiasm.plureutils.event.PlayerEvents;
import com.enthusiasm.plureutils.event.ServerEvents;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureUtilsEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureUtils");
    public static MinecraftServer SERVER;

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureUtils");

        ConfigManager.init();

        ServerEvents.init();
        PlayerEvents.init();

        LOGGER.info("PlureUtils initialized");
    }

    public static void setServer(MinecraftServer server) {
        SERVER = server;
    }
}
