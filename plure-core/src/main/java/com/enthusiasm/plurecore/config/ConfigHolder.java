package com.enthusiasm.plurecore.config;

import com.enthusiasm.plurecore.config.annotation.Comment;
import com.enthusiasm.plurecore.config.annotation.Config;
import com.enthusiasm.plurecore.config.annotation.ConfigEntry;
import com.enthusiasm.plurecore.config.serialization.IDataSerializer;
import com.enthusiasm.plurecore.utils.FolderUtils;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.enthusiasm.plurecore.utils.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHolder<T> {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureCore-Config");
    private final Config definition;
    private final Class<T> configClass;

    private T config;

    public ConfigHolder(Class<T> configClass, Config definition) {
        this.definition = definition;
        this.configClass = configClass;

        if (loadConfig()) {
            saveConfig();
        }
    }

    public Config getDefinition() {
        return definition;
    }

    @NotNull
    public Class<T> getConfigClass() {
        return configClass;
    }

    public void saveConfig() {
        Path configPath = getConfigPath();

        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            LOGGER.error("Произошла ошибка при создании директории.", e);
            return;
        }

        CommentedFileConfig fileConfig = CommentedFileConfig
                .builder(getConfigPath().toFile())
                .preserveInsertionOrder()
                .concurrent()
                .build();

        fileConfig.set("configVersion", "1");

        for (Field field : getConfigClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(this.config);
                String propertyName = field.getName();

                if (field.isAnnotationPresent(ConfigEntry.Category.class)) {
                    propertyName = field.getAnnotation(ConfigEntry.Category.class).value() +  "." + propertyName;
                }

                if (field.isAnnotationPresent(Comment.class)) {
                    Comment comment = field.getAnnotation(Comment.class);
                    fileConfig.setComment(propertyName, " " + comment.value());
                }

                if (field.isAnnotationPresent(ConfigEntry.Serializer.class)) {
                    IDataSerializer serializerClass = ReflectUtils.constructUnsafely(field.getAnnotation(ConfigEntry.Serializer.class).value());
                    value = serializerClass.serialize(value);
                }

                fileConfig.set(propertyName, value);
            } catch (IllegalAccessException e) {
                LOGGER.error("Произошла ошибка при сохранении конфига.", e);
            }
        }

        fileConfig.save();
        fileConfig.close();
    }

    public boolean loadConfig() {
        CommentedFileConfig fileConfig = CommentedFileConfig
                .builder(getConfigPath().toFile())
                .preserveInsertionOrder()
                .concurrent()
                .build();

        fileConfig.checked();
        fileConfig.load();
        fileConfig.close();

        config = ReflectUtils.constructUnsafely(getConfigClass());

        for (Field field : getConfigClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                String propertyName = field.getName();

                if (field.isAnnotationPresent(ConfigEntry.Category.class)) {
                    propertyName = field.getAnnotation(ConfigEntry.Category.class).value() +  "." + propertyName;
                }

                Object data = fileConfig.get(propertyName);

                if (data != null) {
                    if (field.isAnnotationPresent(ConfigEntry.Serializer.class)) {
                        IDataSerializer serializerClass = ReflectUtils.constructUnsafely(field.getAnnotation(ConfigEntry.Serializer.class).value());
                        data = serializerClass.deserialize(data);
                    }

                    if (field.isAnnotationPresent(ConfigEntry.BoundedDiscrete.class) && data instanceof Number numberValue) {
                        long min = field.getAnnotation(ConfigEntry.BoundedDiscrete.class).min();
                        long max = field.getAnnotation(ConfigEntry.BoundedDiscrete.class).max();

                        double doubleValue = numberValue.doubleValue();

                        if (doubleValue < min) {
                            data = min;
                        } else if (doubleValue > max) {
                            data = max;
                        }
                    }

                    field.set(config, data);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Произошла ошибка при загрузке конфига.", e);
                return false;
            }
        }

        return true;
    }

    public T getConfig() {
        return config;
    }

    private Path getConfigPath() {
        return FolderUtils.getConfigFolder().resolve(getDefinition().name() + ".toml");
    }
}
