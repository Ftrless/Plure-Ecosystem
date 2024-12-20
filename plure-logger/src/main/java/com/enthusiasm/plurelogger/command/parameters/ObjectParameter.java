package com.enthusiasm.plurelogger.command.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.SneakyThrows;

import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class ObjectParameter extends AbstractParameter<List<Identifier>> {
    private static final List<Identifier> identifiers = new ArrayList<>();

    static {
        identifiers.addAll(Registries.ITEM.getIds());
        identifiers.addAll(Registries.BLOCK.getIds());
        identifiers.addAll(Registries.ENTITY_TYPE.getIds());
    }

    @SneakyThrows
    @Override
    public List<Identifier> parse(StringReader stringReader) {
        if (stringReader.getString().isEmpty()) {
            return List.of();
        }
        if (stringReader.getString().charAt(stringReader.getCursor()) == '#') {
            stringReader.skip();
            Identifier tagId = IdentifierArgumentType.identifier().parse(stringReader);

            TagKey<Block> blockTag = TagKey.of(Registries.BLOCK.getKey(), tagId);
            if (blockTag != null) {
                return Registries.BLOCK.stream()
                        .map(Registries.BLOCK::getId)
                        .toList();
            }

            TagKey<Item> itemTag = TagKey.of(Registries.ITEM.getKey(), tagId);
            if (itemTag != null) {
                return Registries.ITEM.stream()
                        .map(Registries.ITEM::getId)
                        .toList();
            }

            TagKey<EntityType<?>> entityTypeTag = TagKey.of(Registries.ENTITY_TYPE.getKey(), tagId);
            if (entityTypeTag != null) {
                return Registries.ENTITY_TYPE.stream()
                        .map(Registries.ENTITY_TYPE::getId)
                        .toList();
            }
        }

        return List.of(IdentifierArgumentType.identifier().parse(stringReader));
    }

    @SneakyThrows
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        if (builder.getRemaining().startsWith("#")) {
            List<Identifier> identifiers = new ArrayList<>();
            Registries.BLOCK.streamTags().forEach(tag -> identifiers.add(tag.id()));
            Registries.ITEM.streamTags().forEach(tag -> identifiers.add(tag.id()));
            Registries.ENTITY_TYPE.streamTags().forEach(tag -> identifiers.add(tag.id()));

            return CommandSource.suggestIdentifiers(identifiers, builder.createOffset(builder.getStart() + 1));
        } else {
            return CommandSource.suggestIdentifiers(identifiers, builder);
        }
    }
}
