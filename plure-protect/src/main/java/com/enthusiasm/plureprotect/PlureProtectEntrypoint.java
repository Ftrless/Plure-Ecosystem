package com.enthusiasm.plureprotect;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureProtectEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureUtils");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureProtect");
    }
}
