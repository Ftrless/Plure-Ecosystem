package com.enthusiasm.plurelogger.config;

import static com.enthusiasm.plurelogger.utils.Constants.SEC_IN_MS;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.enthusiasm.plurecore.config.annotation.Comment;
import com.enthusiasm.plurecore.config.annotation.Config;
import com.enthusiasm.plurecore.config.annotation.ConfigEntry;
import com.enthusiasm.plurelogger.config.serialization.ZoneSerialization;

@Config(name = "plure-logger")
public class PLConfig {
    /* COMMON */
    @Comment("Включает логгер.")
    @ConfigEntry.Category("common")
    public Boolean enable = false;

    /* DATABASE */
    @Comment("Имя пользователя.")
    @ConfigEntry.Category("database")
    public String username = "user";

    @Comment("Пароль пользователя.")
    @ConfigEntry.Category("database")
    public String password = "password";

    @Comment("Имя базы данных.")
    @ConfigEntry.Category("database")
    public String database = "db";

    @Comment("Размер пула коннектов.")
    @ConfigEntry.Category("database")
    public Integer poolSize = 20;

    @Comment("Время ожидания освобождения очереди для операций базы данных перед остановкой сервера. (в миллисекундах)")
    @ConfigEntry.Category("database")
    public Integer queueTimeout = SEC_IN_MS * 30;

    @Comment("Задержка проверки очереди на наличие операций перед остановкой сервера. (в миллисекундах)")
    @ConfigEntry.Category("database")
    public Integer queueCheckDelay = SEC_IN_MS * 10;

    @Comment("Автоматическая очистка базы данных. (в днях)")
    @ConfigEntry.Category("database")
    public Integer autoPurge = 30;

    @Comment("Размер максимальной партии за 1 операцию для базы данных.")
    @ConfigEntry.Category("database")
    public Integer batchSize = 128;

    @Comment("Задержка между операциями в партии. (в миллисекундах)")
    @ConfigEntry.Category("database")
    public Integer batchDelay = SEC_IN_MS * 2;

    /* SEARCH */
    @Comment("Размер страницы результатов поиска.")
    @ConfigEntry.Category("search")
    public Integer pageSize = 5;

    @Comment("Часовой пояс для отображения времени.")
    @ConfigEntry.Category("search")
    @ConfigEntry.Serializer(ZoneSerialization.class)
    public ZoneId timeZone = ZoneId.of("UTC");

    @Comment("Максимальное расстояние допустимое для rollback / restore команд.")
    @ConfigEntry.Category("search")
    public Integer maxRange = 100;

    /* COLOR */
    @Comment("Основной цвет.")
    @ConfigEntry.Category("color")
    public String primary = "#009688";

    @Comment("Основной цвет аргументов.")
    @ConfigEntry.Category("color")
    public String primaryVariant = "#52c7b8";

    @Comment("Вторичный цвет.")
    @ConfigEntry.Category("color")
    public String secondary = "#1e88e5";

    @Comment("Вторичный цвет аргументов.")
    @ConfigEntry.Category("color")
    public String secondaryVariant = "#6ab7ff";

    @Comment("Светлый цвет.")
    @ConfigEntry.Category("color")
    public String light = "#c5d6f0";

    /* ACTIONS */
    @Comment("Черный список объектов. Например: \"minecraft:cobblestone\"")
    @ConfigEntry.Category("actions")
    public List<String> objectBlacklist = new ArrayList<>();

    @Comment("Черный список источников действий. Например: \"lava\"")
    @ConfigEntry.Category("actions")
    public List<String> sourceBlacklist = new ArrayList<>();

    @Comment("Черный список миров. Например: \"mincraft:the_end\"")
    @ConfigEntry.Category("actions")
    public List<String> worldBlacklist = new ArrayList<>();

    @Comment("Черный список типов действий. Например: \"block-break\"")
    @ConfigEntry.Category("actions")
    public List<String> typeBlacklist = new ArrayList<>();

    /* DEBUG */
    @Comment("Включает вывод в консоль выполняемых SQL запросов.")
    @ConfigEntry.Category("debug")
    public Boolean showSql = false;

    @Comment("Включает форматирование логгируемых SQL запросов. (отступы, переносы, тд.)")
    @ConfigEntry.Category("debug")
    public Boolean formatSql = false;

    @Comment("Включает показ всех значений подставляемых в выполняемые SQL запросы.")
    @ConfigEntry.Category("debug")
    public Boolean showSqlValues = false;
}
