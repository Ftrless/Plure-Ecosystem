package com.enthusiasm.plurecore.command.types;

public enum ArgumentTypes {
    /** Entity argument (players, mobs, etc.) */
    ENTITY,

    /** Player argument */
    PLAYER,

    /** Offline player argument (auto remap with PlayerUtils) */
    OFFLINE_PLAYER,

    /** Basic string argument */
    STRING,

    /** String word argument */
    SINGLE_WORD,

    /** Greedy string argument */
    GREEDY_STRING,

    /** Integer argument */
    INTEGER,

    /** Boolean argument */
    BOOLEAN,

    /** Float argument */
    FLOAT,

    /** Double argument */
    DOUBLE,

    /** Block position argument */
    BLOCK_POS,

    /** 3D vector argument */
    VEC3,

    /** 2D vector argument */
    VEC2,

    /** Inventory slot argument */
    ITEM_SLOT,

    /** Color argument */
    COLOR,

    /** UUID argument */
    UUID,

    /** Scoreboard objective argument */
    SCOREBOARD_OBJECTIVE,

    /** Score holder argument */
    SCORE_HOLDER,

    /** Team argument */
    TEAM,

    /** Entity anchor argument (feet, eyes) */
    ENTITY_ANCHOR,

    /** Rotation argument */
    ROTATION,

    /** Dimension argument (Nether, Overworld, etc.) */
    DIMENSION,

    /** Time argument */
    TIME
}
