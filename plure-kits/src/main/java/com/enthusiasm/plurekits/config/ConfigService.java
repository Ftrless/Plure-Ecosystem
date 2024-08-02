package com.enthusiasm.plurekits.config;

import com.enthusiasm.plurecore.config.ConfigHolder;

public class ConfigService {
    private static PKConfig config;
    private static ConfigHolder<PKConfig> configHolder;

    public static void init() {
        com.enthusiasm.plurecore.config.ConfigService.register(PKConfig.class);

        configHolder = com.enthusiasm.plurecore.config.ConfigService.getConfigHolder(PKConfig.class);
        config = configHolder.getConfig();
    }

    public static PKConfig getConfig() {
        return config;
    }

    public static void setConfig(PKConfig config) {
        ConfigService.config = config;
    }

    public static ConfigHolder<PKConfig> getConfigHolder() {
        return configHolder;
    }
}
