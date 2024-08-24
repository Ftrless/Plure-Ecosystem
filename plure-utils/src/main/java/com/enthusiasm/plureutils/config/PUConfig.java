package com.enthusiasm.plureutils.config;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import com.enthusiasm.plurecore.config.annotation.Comment;
import com.enthusiasm.plurecore.config.annotation.Config;
import com.enthusiasm.plurecore.config.annotation.ConfigEntry;
import com.enthusiasm.plureutils.config.serialization.BiomeKeySerializer;
import com.enthusiasm.plureutils.config.serialization.BiomeTagSerializer;

@Config(name = "plure-utils")
public class PUConfig {
    @Comment("Специальные миры")
    @ConfigEntry.Category("common")
    public List<String> specialWorlds = List.of("minecraft:mining");

    @Comment("Кулдаун на команду RTP в миллисекундах")
    @ConfigEntry.Category("cooldowns")
    public int rtpCooldown = 30_000;

    @Comment("Кулдаун на глобальные команды в миллисекундах")
    @ConfigEntry.Category("cooldowns")
    public int globalCooldown = 300_000;

    @Comment("Минимальный радиус RTP")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 999_500)
    @ConfigEntry.Category("rtp")
    public int minRadiusRTP = 500;

    @Comment("Максимальный радиус RTP")
    @ConfigEntry.BoundedDiscrete(min = 500, max = 1_000_000)
    @ConfigEntry.Category("rtp")
    public int maxRadiusRTP = 1_000_000;

    @Comment("Черный список биомов для телепортации")
    @ConfigEntry.Category("rtp")
    @ConfigEntry.Serializer(BiomeKeySerializer.class)
    public ReferenceOpenHashSet<RegistryKey<Biome>> blackListedBiomes = new ReferenceOpenHashSet<>(Set.of(
            BiomeKeys.THE_END,
            BiomeKeys.NETHER_WASTES,
            BiomeKeys.SMALL_END_ISLANDS,
            BiomeKeys.THE_VOID
    ));

    @Comment("Черный список тэгов биомов для телепортации")
    @ConfigEntry.Category("rtp")
    @ConfigEntry.Serializer(BiomeTagSerializer.class)
    public ReferenceArrayList<TagKey<Biome>> blackListedBiomeTags = new ReferenceArrayList<>(List.of(
            BiomeTags.IS_OCEAN,
            BiomeTags.IS_DEEP_OCEAN,
            BiomeTags.IS_RIVER
    ));

    @Comment("Включить систему рестартов")
    @ConfigEntry.Category("restart")
    public boolean enableRestarts = false;

    @Comment("Интервал рестарта в миллисекундах")
    @ConfigEntry.Category("restart")
    public int restartInterval = 8 * 3_600_000;

    @Comment("Интервалы напоминаний о рестарте в миллисекундах")
    @ConfigEntry.Category("restart")
    public List<Integer> restartNotifyIntervals = List.of(
            21_600_000,
            10_800_000,
            3_600_000,
            1_800_000,
            600_000,
            300_000,
            60_000,
            30_000,
            15_000,
            10_000,
            9_000,
            8_000,
            7_000,
            6_000,
            5_000,
            4_000,
            3_000,
            2_000,
            1_000
    );

    @Comment("Интервалы напоминаний в тайтле о рестарте в миллисекундах")
    @ConfigEntry.Category("restart")
    public List<Integer> restartTitleNotifyIntervals = List.of(
            10_000,
            9_000,
            8_000,
            7_000,
            6_000,
            5_000,
            4_000,
            3_000,
            2_000,
            1_000
    );

    @Comment("Включить систему автовайпов")
    @ConfigEntry.Category("autowipe")
    public boolean enableAutowipe = false;

    @Comment("Пути до миров для автовайпа")
    @ConfigEntry.Category("autowipe")
    public List<String> autowipeWorlds = List.of("dimensions/minecraft/mining");

    @Comment("Имя папки закешированных миров для копирования")
    @ConfigEntry.Category("autowipe")
    public String cachedWorldsDir = "cachedWorlds";
}
