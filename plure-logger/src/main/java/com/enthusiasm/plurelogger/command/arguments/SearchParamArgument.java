package com.enthusiasm.plurelogger.command.arguments;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.HashMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.command.parameters.*;
import com.enthusiasm.plurelogger.command.parameters.AbstractParameter;
import com.enthusiasm.plurelogger.utils.Negatable;

public class SearchParamArgument {
    private static final Map<String, Parameter<?>> paramSuggesters = new HashMap<>();

    static {
        paramSuggesters.put("action", new NegatableParameter<>(new ActionParameter()));
        paramSuggesters.put("source", new NegatableParameter<>(new SourceParameter()));
        paramSuggesters.put("range", new Parameter<>(new RangeParameter()));
        paramSuggesters.put("object", new NegatableParameter<>(new ObjectParameter()));
        paramSuggesters.put("world", new NegatableParameter<>(new DimensionParameter()));
        paramSuggesters.put("before", new Parameter<>(new TimeParameter()));
        paramSuggesters.put("after", new Parameter<>(new TimeParameter()));
        paramSuggesters.put("rolledback", new Parameter<>(new RollbackStatusParameter()));
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> argument(String name) {
        return CommandManager.argument(name, StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    String input = builder.getInput();
                    int lastSpaceIndex = input.lastIndexOf(' ');
                    char[] inputArr = input.toCharArray();
                    int lastColonIndex = -1;
                    for (int i = inputArr.length - 1; i >= 0; i--) {
                        char c = inputArr[i];
                        if (c == ':') {
                            lastColonIndex = i;
                        } else if (lastColonIndex != -1 && c == ' ') {
                            break;
                        }
                    }
                    if (lastColonIndex == -1) {
                        SuggestionsBuilder offsetBuilder = builder.createOffset(lastSpaceIndex + 1);
                        suggestCriteria(offsetBuilder);
                    } else {
                        String[] spaceSplit = input.substring(0, lastColonIndex).split(" ");
                        String criterion = spaceSplit[spaceSplit.length - 1];
                        String criteriaArg = input.substring(lastColonIndex + 1);
                        if (!paramSuggesters.containsKey(criterion)) {
                            return builder.buildFuture();
                        } else {
                            Parameter<?> suggester = paramSuggesters.get(criterion);
                            int remaining = suggester.getRemaining(criteriaArg);
                            if (remaining > 0) {
                                SuggestionsBuilder offsetBuilder = builder.createOffset(input.length() - remaining + 1);
                                suggestCriteria(offsetBuilder).buildFuture();
                            } else {
                                SuggestionsBuilder offsetBuilder = builder.createOffset(lastColonIndex + 1);
                                return suggester.listSuggestions(context, offsetBuilder);
                            }
                        }
                    }
                    return builder.buildFuture();
                });
    }

    @SuppressWarnings("unchecked")
    public static ActionSearchParams get(String input, ServerCommandSource source) throws CommandSyntaxException {
        StringReader reader = new StringReader(input);
        HashMultimap<String, Object> result = HashMultimap.create();
        while (reader.canRead()) {
            String propertyName = reader.readStringUntil(':').trim();
            Parameter<?> parameter = paramSuggesters.get(propertyName);
            if (parameter == null) {
                throw new SimpleCommandExceptionType(new LiteralMessage("Unknown property value: " + propertyName))
                        .create();
            }
            Object value = parameter instanceof NegatableParameter ?
                    ((NegatableParameter<?>) parameter).parseNegatable(reader) :
                    parameter.parse(reader);
            result.put(propertyName, value);
        }

        ActionSearchParams.Builder builder = new ActionSearchParams.Builder();

        for (Map.Entry<String, Object> entry : result.entries()) {
            String param = entry.getKey();
            Object value = entry.getValue();

            switch (param) {
                case "range" -> {
                    int range = (int) value - 1;
                    builder.setBounds(BlockBox.create(
                            BlockPos.ofFloored(source.getPosition()).subtract(new Vec3i(range, range, range)),
                            BlockPos.ofFloored(source.getPosition()).add(new Vec3i(range, range, range))
                    ));
                    Negatable<Identifier> world = Negatable.allow(source.getWorld().getRegistryKey().getValue());
                    if (builder.getWorlds() == null) builder.setWorlds(new HashSet<>(Collections.singleton(world)));
                    else builder.getWorlds().add(world);
                }
                case "world" -> {
                    Negatable<Identifier> world = (Negatable<Identifier>) value;
                    if (builder.getWorlds() == null) builder.setWorlds(new HashSet<>(Collections.singleton(world)));
                    else builder.getWorlds().add(world);
                }
                case "object" -> {
                    Negatable<List<Identifier>> objectIds = (Negatable<List<Identifier>>) value;
                    Set<Negatable<Identifier>> objectSet = new HashSet<>();
                    for (Identifier id : objectIds.property()) {
                        objectSet.add(objectIds.allowed() ? Negatable.allow(id) : Negatable.deny(id));
                    }
                    if (builder.getObjects() == null) {
                        builder.setObjects(objectSet);
                    } else {
                        builder.getObjects().addAll(objectSet);
                    }
                }
                case "source" -> {
                    Negatable<String> sourceInput = (Negatable<String>) value;
                    if (sourceInput.property().startsWith("@")) {
                        Negatable<String> nonPlayer = new Negatable<>(sourceInput.property().trim().substring(1), sourceInput.allowed());
                        if (builder.getSourceNames() == null) {
                            builder.setSourceNames(new HashSet<>(Collections.singleton(nonPlayer)));
                        } else {
                            builder.getSourceNames().add(nonPlayer);
                        }
                    } else {
                        Optional<GameProfile> profile = source.getServer().getUserCache().findByName(sourceInput.property());
                        UUID id = profile.isPresent() ? profile.get().getId() : UUID.randomUUID();

                        if (id != null) {
                            Negatable<UUID> playerIdEntry = new Negatable<>(id, sourceInput.allowed());
                            if (builder.getSourcePlayerIds() == null) {
                                builder.setSourcePlayerIds(new HashSet<>(Collections.singleton(playerIdEntry)));
                            } else {
                                builder.getSourcePlayerIds().add(playerIdEntry);
                            }
                        }
                    }
                }
                case "action" -> {
                    Negatable<String> action = (Negatable<String>) value;
                    if (builder.getActions() == null) {
                        builder.setActions(new HashSet<>(Collections.singleton(action)));
                    } else {
                        builder.getActions().add(action);
                    }
                }
                case "before" -> builder.setBefore((Instant) value);
                case "after" -> builder.setAfter((Instant) value);
                case "rolledback" -> builder.setRolledBack((Boolean) value);
            }
        }

        return builder.build();
    }

    public static ActionSearchParams get(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        String input = StringArgumentType.getString(context, name);
        return get(input, context.getSource());
    }

    private static SuggestionsBuilder suggestCriteria(SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        for (String param : paramSuggesters.keySet()) {
            if (param.startsWith(input)) {
                builder.suggest(param + ":", Text.translatable("text.ledger.parameter." + param + ".description"));
            }
        }
        return builder;
    }

    private static class Parameter<T> {
        private final AbstractParameter<T> parameter;

        public Parameter(AbstractParameter<T> parameter) {
            this.parameter = parameter;
        }

        public CompletableFuture<Suggestions> listSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            try {
                return parameter.getSuggestions(context, builder);
            } catch (CommandSyntaxException e) {
                return builder.buildFuture();
            }
        }

        public int getRemaining(String s) {
            StringReader reader = new StringReader(s);
            parameter.parse(reader);
            return reader.getRemainingLength();
        }

        public T parse(StringReader reader) {
            return parameter.parse(reader);
        }
    }

    private static class NegatableParameter<T> extends Parameter<T> {
        public NegatableParameter(AbstractParameter<T> parameter) {
            super(parameter);
        }

        @Override
        public CompletableFuture<Suggestions> listSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            SuggestionsBuilder offsetBuilder = builder.getRemaining().startsWith("!")
                    ? builder.createOffset(builder.getStart() + 1)
                    : builder;
            return super.listSuggestions(context, offsetBuilder);
        }

        @Override
        public int getRemaining(String s) {
            String input = s.startsWith("!") ? s.substring(1) : s;
            return super.getRemaining(input);
        }

        public Negatable<T> parseNegatable(StringReader reader) {
            if (reader.getString().isEmpty()) return Negatable.allow(super.parse(reader));
            if (reader.getString().charAt(reader.getCursor()) == '!') {
                reader.skip();
                return Negatable.deny(super.parse(reader));
            } else {
                return Negatable.allow(super.parse(reader));
            }
        }
    }
}
