package com.enthusiasm.plureeconomy;

import com.enthusiasm.plurecore.database.connection.ConnectionCredentials;
import com.enthusiasm.plurecore.database.connection.HikariService;
import com.enthusiasm.plureeconomy.config.ConfigService;
import com.enthusiasm.plureeconomy.config.PEConfig;
import com.enthusiasm.plureeconomy.database.DatabaseService;
import com.enthusiasm.plureeconomy.event.PlayerEvents;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class PlureEconomyEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureEconomy");
    private static DatabaseService databaseService;

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureEconomy");

        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);

        ConfigService.init();
        PlayerEvents.init();

        PEConfig config = ConfigService.getConfig();
        databaseService = new DatabaseService(
                new HikariService(
                        new ConnectionCredentials(
                                config.address,
                                config.database,
                                config.username,
                                config.password,
                                config.maxPoolSize,
                                config.minIdleConnections,
                                config.maxLifetime,
                                config.keepAliveTime,
                                config.connectionTimeout,
                                new HashMap<>()
                        )
                )
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStopping(MinecraftServer minecraftServer) {
        getDatabaseService().shutdown();
    }

    public static DatabaseService getDatabaseService() {
        return databaseService;
    }
}
