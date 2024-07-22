package com.enthusiasm.plureutils.config;

import com.enthusiasm.plurecore.config.annotation.Comment;
import com.enthusiasm.plurecore.config.annotation.Config;
import com.enthusiasm.plurecore.config.annotation.ConfigEntry;
import com.enthusiasm.plureutils.config.serialization.BiomeKeySerializer;
import com.enthusiasm.plureutils.config.serialization.BiomeTagSerializer;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;
import java.util.Set;

@Config(name = "plure-utils")
public class PUConfig {
    @Comment("Кулдаун на команду RTP в миллисекундах")
    @ConfigEntry.Category("cooldowns")
    public int rtpCooldown = 60_000;

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

    @Comment("Включить рестарты")
    @ConfigEntry.Category("restart")
    public boolean enableRestarts = false;

    @Comment("Интервал рестарта в миллисекундах")
    @ConfigEntry.Category("restart")
    public int restartInterval = 8 * 3_600_000;
}
