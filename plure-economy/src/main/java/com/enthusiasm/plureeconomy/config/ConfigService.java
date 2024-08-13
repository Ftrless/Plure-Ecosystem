package com.enthusiasm.plureeconomy.config;

import com.enthusiasm.plurecore.config.ConfigHolder;

public class ConfigService {
    private static PEConfig config;
    private static ConfigHolder<PEConfig> configHolder;

    public static void init() {
        com.enthusiasm.plurecore.config.ConfigService.register(PEConfig.class);

        configHolder = com.enthusiasm.plurecore.config.ConfigService.getConfigHolder(PEConfig.class);
        config = configHolder.getConfig();
    }

    public static PEConfig getConfig() {
        return config;
    }

    public static void setConfig(PEConfig config) {
        ConfigService.config = config;
    }

    public static ConfigHolder<PEConfig> getConfigHolder() {
        return configHolder;
    }
}
