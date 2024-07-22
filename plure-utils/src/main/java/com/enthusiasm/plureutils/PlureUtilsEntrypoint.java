package com.enthusiasm.plureutils;

import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.service.RandomTeleportService;
import com.enthusiasm.plureutils.service.RestartService;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureUtilsEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureUtils");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureUtils");

        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(this::onTick);
    }

    private void onServerStarting(MinecraftServer minecraftServer) {
        DataManager.onInitialize(minecraftServer);
    }
    private void onServerStarted(MinecraftServer minecraftServer) {
        RestartService.onInitialize(minecraftServer);
    }
    private void onTick(MinecraftServer server) {
        RandomTeleportService.update();
    }
}
