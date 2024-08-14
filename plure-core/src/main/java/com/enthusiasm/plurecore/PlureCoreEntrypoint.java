package com.enthusiasm.plurecore;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.event.PlayerEvents;
import com.enthusiasm.plurecore.utils.ThreadUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureCoreEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureCore");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureCore");

        CacheService.init();
        PlayerEvents.init();

        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStarting(MinecraftServer minecraftServer) {
        ThreadUtils.setServerInstance(minecraftServer);
    }

    private void onServerStopping(MinecraftServer minecraftServer) {
        ThreadUtils.shutdown();
    }
}
