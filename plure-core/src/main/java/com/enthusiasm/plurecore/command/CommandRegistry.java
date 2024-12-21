package com.enthusiasm.plurecore.command;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.enthusiasm.plurecore.command.annotations.*;
import com.enthusiasm.plurecore.command.types.ArgumentTypeMapper;
import com.enthusiasm.plurecore.command.types.ArgumentTypes;
import com.enthusiasm.plurecore.permission.AbstractPermissions;
import com.enthusiasm.plurecore.utils.BrigadierUtils;
import com.enthusiasm.plurecore.utils.ReflectUtils;

public class CommandRegistry {
    private final Map<String, CommandNode<ServerCommandSource>> nodes = new HashMap<>();
    private AbstractPermissions permissions;

    public static CommandNode<ServerCommandSource> rootNode;

    public static CommandRegistry builder() {
        return new CommandRegistry();
    }

    public CommandRegistry permissionHandler(Class<? extends AbstractPermissions> permissionHandler) {
        this.permissions = ReflectUtils.constructUnsafely(permissionHandler);
        return this;
    }

    public CommandRegistry node(String ...nodes) {
        for (String node : nodes) {
            if (!this.nodes.containsKey(node)) {
                this.nodes.put(node, CommandRegistryWrapper.buildNode(rootNode, node));
            }
        }

        return this;
    }

    public void build(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");

        Reflections reflections = new Reflections("com.enthusiasm." + modId.replace("-", ""), Scanners.TypesAnnotated);
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(Command.class);

        commandClasses.forEach(this::registerCommand);
    }

    private void registerCommand(Class<?> commandClass) {
        if (commandClass == null) return;

        try {
            Command commandAnnotation = commandClass.getAnnotation(Command.class);
            LiteralArgumentBuilder<ServerCommandSource> literalBuilder = LiteralArgumentBuilder.literal(commandAnnotation.name());
            applyCommandPermissions(commandClass, literalBuilder);
            buildCommandArguments(commandClass, literalBuilder);
            CommandNode<ServerCommandSource> commandNode = getOrDefaultCommandNode(commandAnnotation);
            registerLiteralNode(commandAnnotation, literalBuilder, commandNode);
        } catch (Exception e) {
            PlureCoreEntrypoint.LOGGER.error("Error while registering command class {}", commandClass.getName(), e);
        }
    }

    private void applyCommandPermissions(Class<?> commandClass, LiteralArgumentBuilder<ServerCommandSource> literalBuilder) {
        if (commandClass.isAnnotationPresent(CommandPermission.class)) {
            CommandPermission permissionAnnotation = commandClass.getAnnotation(CommandPermission.class);
            literalBuilder.requires(source -> this.permissions.checkPermission(source, permissionAnnotation.requirement(), permissions));
        }
    }

    private void buildCommandArguments(Class<?> commandClass, LiteralArgumentBuilder<ServerCommandSource> literalBuilder) throws NoSuchFieldException, IllegalAccessException {
        RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = null;

        for (Field field : commandClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CommandArgument.class)) {
                CommandArgument argumentAnnotation = field.getAnnotation(CommandArgument.class);
                argumentBuilder = processCommandArgument(commandClass, literalBuilder, argumentBuilder, field, argumentAnnotation);
            }
        }

        applyExecute(commandClass, getOrDefaultArgumentBuilder(argumentBuilder, literalBuilder), null, false);
    }

    private RequiredArgumentBuilder<ServerCommandSource, ?> processCommandArgument(Class<?> commandClass, LiteralArgumentBuilder<ServerCommandSource> literalBuilder,
                                                                                   RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder, Field field, CommandArgument argumentAnnotation) throws NoSuchFieldException, IllegalAccessException {
        applyExecute(commandClass, getOrDefaultArgumentBuilder(argumentBuilder, literalBuilder), argumentAnnotation.name(), true);
        argumentBuilder = applyArgument(field, argumentBuilder);
        return argumentBuilder;
    }

    private void registerLiteralNode(Command commandAnnotation, LiteralArgumentBuilder<ServerCommandSource> literalBuilder,
                                     CommandNode<ServerCommandSource> commandNode) {
        LiteralCommandNode<ServerCommandSource> literalNode = literalBuilder.build();
        commandNode.addChild(literalNode);

        Arrays.stream(commandAnnotation.aliases())
                .map(alias -> BrigadierUtils.buildRedirect(alias, literalNode))
                .forEach(commandNode::addChild);
    }

    private RequiredArgumentBuilder<ServerCommandSource, ?> applyArgument(Field field, RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder) throws NoSuchFieldException, IllegalAccessException {
        CommandArgument argumentAnnotation = field.getAnnotation(CommandArgument.class);
        ArgumentType<?> argumentType = ArgumentTypeMapper.getArgumentType(argumentAnnotation.type());

        argumentBuilder = argumentBuilder == null
                ? argument(argumentAnnotation.name(), argumentType)
                : argumentBuilder.then(argument(argumentAnnotation.name(), argumentType));

        applyArgumentSuggestions(field, argumentBuilder);
        return argumentBuilder;
    }

    private void applyArgumentSuggestions(Field field, RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder) throws NoSuchFieldException, IllegalAccessException {
        if (field.isAnnotationPresent(ArgumentSuggestion.class)) {
            ArgumentSuggestion suggestsAnnotation = field.getAnnotation(ArgumentSuggestion.class);
            Field providerField = suggestsAnnotation.provider().getField(suggestsAnnotation.providerName());
            SuggestionProvider<ServerCommandSource> provider = (SuggestionProvider<ServerCommandSource>) providerField.get(null);
            argumentBuilder.suggests(provider);
        }
    }

    private void applyExecute(Class<?> commandClass, ArgumentBuilder<ServerCommandSource, ?> commandArgumentBuilder, String argName, boolean applyBefore) {
        for (Method method : commandClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandExecutor.class)) {
                CommandExecutor commandExecutorAnnotation = method.getAnnotation(CommandExecutor.class);

                if (shouldApplyExecutor(argName, applyBefore, commandExecutorAnnotation)) {
                    commandArgumentBuilder.executes(context -> executeCommandMethod(commandClass, method, context, applyBefore));
                }
            }
        }
    }

    private boolean shouldApplyExecutor(String argName, boolean applyBefore, CommandExecutor commandExecutorAnnotation) {
        return (argName == null && commandExecutorAnnotation.before().isEmpty()) ||
                (argName != null && applyBefore && argName.equals(commandExecutorAnnotation.before()));
    }

    private int executeCommandMethod(Class<?> commandClass, Method method, CommandContext<ServerCommandSource> context, boolean applyBefore) {
        try {
            Object commandInstance = ReflectUtils.constructUnsafely(commandClass);

            if (!applyBefore) {
                Arrays.stream(commandClass.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(CommandArgument.class))
                        .forEach(field -> injectArgument(commandInstance, field, context));
            }

            return (int) method.invoke(commandInstance, context);
        } catch (IllegalAccessException | InvocationTargetException e) {
            PlureCoreEntrypoint.LOGGER.error("Error while invoking command method: ", e);
            return -1;
        }
    }

    private void injectArgument(Object commandInstance, Field field, CommandContext<ServerCommandSource> context) {
        CommandArgument argAnnotation = field.getAnnotation(CommandArgument.class);
        ArgumentTypes argType = argAnnotation.type();

        try {
            Object argumentValue = ArgumentTypeMapper.getArgumentResult(context, argAnnotation.name(), argType);
            field.setAccessible(true);
            field.set(commandInstance, argumentValue);
        } catch (CommandSyntaxException | IllegalAccessException ignored) {}
    }

    private CommandNode<ServerCommandSource> getOrDefaultCommandNode(Command commandAnnotation) {
        return commandAnnotation.node().equals("root") ? rootNode : nodes.getOrDefault(commandAnnotation.node(), rootNode);
    }

    private ArgumentBuilder<ServerCommandSource, ?> getOrDefaultArgumentBuilder(RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder, LiteralArgumentBuilder<ServerCommandSource> literalBuilder) {
        return argumentBuilder == null ? literalBuilder : argumentBuilder;
    }
}
