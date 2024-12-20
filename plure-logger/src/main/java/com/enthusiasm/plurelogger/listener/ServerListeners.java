package com.enthusiasm.plurelogger.listener;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.PlureLoggerEntrypoint;
import com.enthusiasm.plurelogger.registry.ActionRegistry;
import com.enthusiasm.plurelogger.registry.SourcesRegistry;
import com.enthusiasm.plurelogger.registry.WorldRegistry;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.Logger;

public class ServerListeners {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerListeners::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerListeners::onServerStopped);
    }

    private static void onServerStarting(MinecraftServer server) {
        PlureLoggerEntrypoint.SERVER = server;

        DatabaseService.setup();
        ActionRegistry.registerDefaultTypes();
        WorldRegistry.registerWorlds();
        SourcesRegistry.registerSources();

        Set<Identifier> idSet = new HashSet<>();
        idSet.addAll(Registries.BLOCK.getIds());
        idSet.addAll(Registries.ITEM.getIds());
        idSet.addAll(Registries.ENTITY_TYPE.getIds());

        ThreadUtils.runAsync(() -> {
            Logger.logInfo("Inserting {} registry keys into the database...", idSet.size());
            DatabaseService.insertIdentifiers(idSet);
            Logger.logInfo("Registry insert complete");

            DatabaseService.setupCache();
            DatabaseService.autoPurge();
        });

        ActionQueueService.start();
    }

    private static void onServerStopped(MinecraftServer server) {
        ActionQueueService.stop();

        if (ActionQueueService.getSize() > 0) {
            ActionQueueService.drainBatch();
        }

        Logger.logInfo("Queue successfully drained.");
        DatabaseService.stop();
    }
}
