package com.enthusiasm.plureproxy;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureProxyEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureProxyEntrypoint");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureProxyEntrypoint");
    }
}
