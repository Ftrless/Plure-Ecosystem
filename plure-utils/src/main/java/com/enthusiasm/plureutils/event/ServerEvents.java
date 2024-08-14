package com.enthusiasm.plureutils.event;

import com.enthusiasm.plureutils.CommandRegistry;
import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.data.DataManager;
import com.enthusiasm.plureutils.service.AutowipeService;
import com.enthusiasm.plureutils.service.RandomTeleportService;
import com.enthusiasm.plureutils.service.RestartService;
import com.enthusiasm.plureutils.service.VanishService;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerEvents {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);

        ServerLifecycleEvents.SERVER_STARTING.register(ServerEvents::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEvents::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerEvents::onServerStopped);

        ServerTickEvents.END_SERVER_TICK.register(ServerEvents::onTick);
    }

    private static void onServerStarting(MinecraftServer minecraftServer) {
        PlureUtilsEntrypoint.setServer(minecraftServer);

        DataManager.onInitialize();
        AutowipeService.onInitializeBar();
        VanishService.onInitialize();
    }

    private static void onServerStarted(MinecraftServer minecraftServer) {
        RestartService.onInitialize(minecraftServer);
    }

    private static void onServerStopped(MinecraftServer minecraftServer) {
        AutowipeService.runAutoWipe();
    }

    private static void onTick(MinecraftServer server) {
        RandomTeleportService.update();
    }
}
