package com.enthusiasm.plureeconomy.config;

import com.enthusiasm.plurecore.config.annotation.Comment;
import com.enthusiasm.plurecore.config.annotation.Config;
import com.enthusiasm.plurecore.config.annotation.ConfigEntry;

@Config(name = "plure-economy")
public class PEConfig {
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

    @Comment("Имя таблицы")
    @ConfigEntry.Category("database")
    public String tableName = "economy";

    @Comment("Максимальный размер пула подключений")
    @ConfigEntry.Category("database")
    public int maxPoolSize = 10;

    @Comment("Минимальное количество удерживаемых подключений")
    @ConfigEntry.Category("database")
    public int minIdleConnections = 6;

    @Comment("Максимальное время жизни подключения (в миллисекундах)")
    @ConfigEntry.Category("database")
    public int maxLifetime = 1_800_000;

    @Comment("Время удержания жизни одного подключения (в миллисекундах)")
    @ConfigEntry.Category("database")
    public int keepAliveTime = 0;

    @Comment("Время ожидания соединения (в миллисекундах)")
    @ConfigEntry.Category("database")
    public int connectionTimeout = 10_000;
}
