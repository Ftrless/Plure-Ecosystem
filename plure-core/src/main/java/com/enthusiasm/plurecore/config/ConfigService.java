package com.enthusiasm.plurecore.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.enthusiasm.plurecore.config.annotation.Config;

public class ConfigService {
    private static final Map<Class<?>, ConfigHolder<?>> holders = new HashMap<>();

    public static <T> ConfigHolder<T> register(Class<T> configClass) {
        Objects.requireNonNull(configClass);

        if (holders.containsKey(configClass)) {
            throw new RuntimeException(String.format("Конфиг '%s' уже зарегистрирован", configClass));
        }

        Config definition = configClass.getAnnotation(Config.class);
        if (definition == null) {
            throw new RuntimeException(String.format("@Config аннотация не найдена на %s!", configClass));
        }

        ConfigHolder<T> holder = new ConfigHolder<>(configClass, definition);
        holders.put(configClass, holder);

        return holder;
    }

    public static <T> ConfigHolder<T> getConfigHolder(Class<T> configClass) {
        Objects.requireNonNull(configClass);

        if (!holders.containsKey(configClass)) {
            throw new RuntimeException(String.format("Конфиг '%s' не зарегистрирован", configClass));
        }

        return (ConfigHolder<T>) holders.get(configClass);
    }
}
