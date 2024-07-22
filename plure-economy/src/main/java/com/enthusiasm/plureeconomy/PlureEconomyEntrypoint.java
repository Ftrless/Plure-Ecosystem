package com.enthusiasm.plureeconomy;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureEconomyEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureEconomy");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureEconomy");
    }
}
