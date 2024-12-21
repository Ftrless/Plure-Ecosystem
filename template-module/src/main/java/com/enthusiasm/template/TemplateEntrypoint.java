package com.enthusiasm.template;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateEntrypoint implements DedicatedServerModInitializer {
    public static final String MOD_ID = "templateid";
    public static final Logger LOGGER = LoggerFactory.getLogger("TemplateEntrypoint");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing TemplateEntrypoint");
    }
}
