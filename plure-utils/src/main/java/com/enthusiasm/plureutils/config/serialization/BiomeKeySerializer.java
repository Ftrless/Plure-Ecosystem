package com.enthusiasm.plureutils.config.serialization;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import com.enthusiasm.plurecore.config.serialization.IDataSerializer;

public class BiomeKeySerializer implements IDataSerializer<ReferenceOpenHashSet<RegistryKey<Biome>>, List<String>> {
    @Override
    public List<String> serialize(ReferenceOpenHashSet<RegistryKey<Biome>> value) {
        return value.stream()
                .map(biomeKey -> biomeKey.getValue().toString())
                .collect(Collectors.toList());
    }

    @Override
    public ReferenceOpenHashSet<RegistryKey<Biome>> deserialize(List<String> value) {
        Set<RegistryKey<Biome>> biomes = value.stream()
                .map(s -> RegistryKey.of(RegistryKeys.BIOME, new Identifier(s)))
                .collect(Collectors.toSet());
        return new ReferenceOpenHashSet<>(biomes);
    }
}
