package com.enthusiasm.plurekits;

import com.enthusiasm.plurecore.database.connection.ConnectionCredentials;
import com.enthusiasm.plurecore.database.connection.HikariService;
import com.enthusiasm.plurekits.config.ConfigService;
import com.enthusiasm.plurekits.config.PKConfig;
import com.enthusiasm.plurekits.database.DatabaseService;
import com.enthusiasm.plurekits.event.PlayerEvents;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureKitsEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureKits");
    private static DatabaseService databaseService;

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureKits");

        ConfigService.init();

        KitService.init();
        PlayerEvents.init();

        PKConfig config = ConfigService.getConfig();

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
                                config.properties
                        )
                )
        );

        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStopping(MinecraftServer minecraftServer) {
        getDatabaseService().shutdown();
    }

    public static DatabaseService getDatabaseService() {
        return databaseService;
    }
}
