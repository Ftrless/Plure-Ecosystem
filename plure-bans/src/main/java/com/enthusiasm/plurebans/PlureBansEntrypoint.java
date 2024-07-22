package com.enthusiasm.plurebans;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureBansEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureBans");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureBans");
    }
}
