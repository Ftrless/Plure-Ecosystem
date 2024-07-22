package com.enthusiasm.plurechat;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureChatEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureChat");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureUtils");
    }
}
