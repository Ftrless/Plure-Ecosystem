package com.enthusiasm.plurekits;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureKitsEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureKits");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureKits");
    }
}
