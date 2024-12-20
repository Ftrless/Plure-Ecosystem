package com.enthusiasm.plurelogger;

import net.fabricmc.api.DedicatedServerModInitializer;

import net.minecraft.server.MinecraftServer;

import com.enthusiasm.plurelogger.config.ConfigWrapper;
import com.enthusiasm.plurelogger.registry.ModRegistry;
import com.enthusiasm.plurelogger.utils.Logger;

public class PlureLoggerEntrypoint implements DedicatedServerModInitializer {
    public static MinecraftServer SERVER;

    @Override
    public void onInitializeServer() {
        Logger.logInfo("Initializing PlureLogger");

        if (ensureEnabled()) {
            ModRegistry.registerEvents();
        }
    }

    private static boolean ensureEnabled() {
        return ConfigWrapper.getConfig().enable;
    }
}
