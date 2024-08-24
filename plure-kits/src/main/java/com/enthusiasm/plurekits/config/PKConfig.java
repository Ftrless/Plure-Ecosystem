package com.enthusiasm.plurekits.config;

import java.util.HashMap;
import java.util.Map;

import com.enthusiasm.plurecore.config.annotation.Comment;
import com.enthusiasm.plurecore.config.annotation.Config;
import com.enthusiasm.plurecore.config.annotation.ConfigEntry;
import com.enthusiasm.plurekits.config.serialization.HashMapSerializer;

@Config(name = "plure-kits")
public class PKConfig {
    @Comment("Тип сохранения китов")
    @ConfigEntry.Category("kits")
    public String kitSaveType = "nbt";

    @Comment("Тип сохранения перезарядок китов")
    @ConfigEntry.Category("kits")
    public String cooldownSaveType = "nbt";

    @Comment("Адрес сервера базы данных")
    @ConfigEntry.Category("database")
    public String address = "127.0.0.1";

    @Comment("Имя базы данных")
    @ConfigEntry.Category("database")
    public String database = "database";

    @Comment("Имя пользователя")
    @ConfigEntry.Category("database")
    public String username = "root";

    @Comment("Пароль пользователя")
    @ConfigEntry.Category("database")
    public String password = "password";

    @Comment("Максимальный размер пула подключений")
    @ConfigEntry.Category("database")
    public int maxPoolSize = 10;

    @Comment("Минимальное количество удерживаемых подключений")
    @ConfigEntry.Category("database")
    public int minIdleConnections = 4;

    @Comment("Максимальное время жизни подключения (в миллисекундах)")
    @ConfigEntry.Category("database")
    public int maxLifetime = 1_800_000;

    @Comment("Время удержания жизни одного подключения (в миллисекундах)")
    @ConfigEntry.Category("database")
    public int keepAliveTime = 0;

    @Comment("Время ожидания соединения (в миллисекундах)")
    @ConfigEntry.Category("database")
    public int connectionTimeout = 10_000;

    @Comment("Дополнительные свойства для подключения")
    @ConfigEntry.Category("database")
    @ConfigEntry.Serializer(value = HashMapSerializer.class)
    public Map<String, String> properties = new HashMap<>();
}
