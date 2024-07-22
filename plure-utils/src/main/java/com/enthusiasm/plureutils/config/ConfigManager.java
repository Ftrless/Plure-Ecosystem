package com.enthusiasm.plureutils.config;

import com.enthusiasm.plurecore.config.ConfigHolder;
import com.enthusiasm.plurecore.config.ConfigService;

public class ConfigManager {
    private static PUConfig config;
    private static ConfigHolder<PUConfig> configHolder;

    public static void init() {
        ConfigService.register(PUConfig.class);

        configHolder = ConfigService.getConfigHolder(PUConfig.class);
        config = configHolder.getConfig();
    }

    public static PUConfig getConfig() {
        return config;
    }

    public static void setConfig(PUConfig config) {
        ConfigManager.config = config;
    }

    public static ConfigHolder<PUConfig> getConfigHolder() {
        return configHolder;
    }
}
