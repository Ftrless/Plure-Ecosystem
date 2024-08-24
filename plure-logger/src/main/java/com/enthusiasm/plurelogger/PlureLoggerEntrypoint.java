package com.enthusiasm.plurelogger;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enthusiasm.plurelogger.log.LogManager;

public class PlureLoggerEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureLogger");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureLogger");

        LogManager.init();
        LogManager.initLoggers();
    }
}
