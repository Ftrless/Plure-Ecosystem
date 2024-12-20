package com.enthusiasm.plurelogger.config;


import lombok.Getter;

import com.enthusiasm.plurecore.config.ConfigHolder;
import com.enthusiasm.plurecore.config.ConfigService;

public class ConfigWrapper {
    @Getter
    private static PLConfig config;

    public static void init() {
        ConfigHolder<PLConfig> configHolder = ConfigService.register(PLConfig.class);
        config = configHolder.getConfig();
    }

}
