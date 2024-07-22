package com.enthusiasm.plureutils.config.serialization;

import com.enthusiasm.plurecore.config.serialization.IDataSerializer;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.stream.Collectors;

public class BiomeTagSerializer implements IDataSerializer<ReferenceArrayList<TagKey<Biome>>, List<String>> {
    @Override
    public List<String> serialize(ReferenceArrayList<TagKey<Biome>> value) {
        return value.stream()
                .map(biomeKey -> biomeKey.id().toString())
                .collect(Collectors.toList());
    }

    @Override
    public ReferenceArrayList<TagKey<Biome>> deserialize(List<String> value) {
        List<TagKey<Biome>> tags = value.stream()
                .map(s -> TagKey.of(RegistryKeys.BIOME, new Identifier(s)))
                .collect(Collectors.toList());
        return new ReferenceArrayList<>(tags);
    }
}
