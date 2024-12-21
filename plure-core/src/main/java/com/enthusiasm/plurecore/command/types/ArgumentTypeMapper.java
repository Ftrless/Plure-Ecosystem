package com.enthusiasm.plurecore.command.types;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.*;
import net.minecraft.server.command.ServerCommandSource;

import com.enthusiasm.plurecore.utils.PlayerUtils;

public class ArgumentTypeMapper {
    public static ArgumentType<?> getArgumentType(ArgumentTypes type) {
        return switch (type) {
            case ENTITY -> EntityArgumentType.entity();
            case PLAYER -> EntityArgumentType.player();
            case OFFLINE_PLAYER, SINGLE_WORD -> StringArgumentType.word();
            case STRING -> StringArgumentType.string();
            case GREEDY_STRING -> StringArgumentType.greedyString();
            case INTEGER -> IntegerArgumentType.integer();
            case BOOLEAN -> BoolArgumentType.bool();
            case FLOAT -> FloatArgumentType.floatArg();
            case DOUBLE -> DoubleArgumentType.doubleArg();
            case BLOCK_POS -> BlockPosArgumentType.blockPos();
            case VEC3 -> Vec3ArgumentType.vec3();
            case VEC2 -> Vec2ArgumentType.vec2();
            case ITEM_SLOT -> ItemSlotArgumentType.itemSlot();
            case COLOR -> ColorArgumentType.color();
            case UUID -> UuidArgumentType.uuid();
            case SCOREBOARD_OBJECTIVE -> ScoreboardObjectiveArgumentType.scoreboardObjective();
            case SCORE_HOLDER -> ScoreHolderArgumentType.scoreHolder();
            case TEAM -> TeamArgumentType.team();
            case ENTITY_ANCHOR -> EntityAnchorArgumentType.entityAnchor();
            case ROTATION -> RotationArgumentType.rotation();
            case DIMENSION -> DimensionArgumentType.dimension();
            case TIME -> TimeArgumentType.time();
        };
    }

    public static Object getArgumentResult(CommandContext<ServerCommandSource> context, String argumentName, ArgumentTypes type) throws CommandSyntaxException {
        return switch (type) {
            case ENTITY -> EntityArgumentType.getEntity(context, argumentName);
            case PLAYER -> EntityArgumentType.getPlayer(context, argumentName);
            case OFFLINE_PLAYER -> PlayerUtils.getPlayer(context, argumentName);
            case STRING, SINGLE_WORD, GREEDY_STRING -> StringArgumentType.getString(context, argumentName);
            case INTEGER -> IntegerArgumentType.getInteger(context, argumentName);
            case BOOLEAN -> BoolArgumentType.getBool(context, argumentName);
            case FLOAT -> FloatArgumentType.getFloat(context, argumentName);
            case DOUBLE -> DoubleArgumentType.getDouble(context, argumentName);
            case BLOCK_POS -> BlockPosArgumentType.getBlockPos(context, argumentName);
            case VEC3 -> Vec3ArgumentType.getVec3(context, argumentName);
            case VEC2 -> Vec2ArgumentType.getVec2(context, argumentName);
            case ITEM_SLOT -> ItemSlotArgumentType.getItemSlot(context, argumentName);
            case COLOR -> ColorArgumentType.getColor(context, argumentName);
            case UUID -> UuidArgumentType.getUuid(context, argumentName);
            case SCOREBOARD_OBJECTIVE -> ScoreboardObjectiveArgumentType.getObjective(context, argumentName);
            case SCORE_HOLDER -> ScoreHolderArgumentType.getScoreHolder(context, argumentName);
            case TEAM -> TeamArgumentType.getTeam(context, argumentName);
            case ENTITY_ANCHOR -> EntityAnchorArgumentType.getEntityAnchor(context, argumentName);
            case ROTATION -> RotationArgumentType.getRotation(context, argumentName);
            case DIMENSION -> DimensionArgumentType.getDimensionArgument(context, argumentName);
            case TIME -> null;
        };
    }
}
