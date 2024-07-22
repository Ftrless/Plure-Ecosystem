package com.enthusiasm.plurecore;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureCoreEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureCore");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureUtils");
    }
}
