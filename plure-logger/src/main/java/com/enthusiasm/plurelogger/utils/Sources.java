package com.enthusiasm.plurelogger.utils;

import lombok.Getter;

@Getter
public enum Sources {
    FIRE("fire"),
    GRAVITY("gravity"),
    EXPLOSION("explosion"),
    PLAYER("player"),
    BROKE("broke"),
    DECAY("decay"),
    DRY("dry"),
    WET("wet"),
    MELT("melt"),
    FROST_WALKER("frost_walker"),
    REDSTONE("redstone"),
    FLUID("fluid"),
    FILL("fill"),
    DRAIN("drain"),
    DRIP("drip"),
    SNOW("snow"),
    REMOVE("remove"),
    TRAMPLE("trample"),
    EXTINGUISH("extinguish"),
    INSERT("insert"),
    INTERACT("interact"),
    CONSUME("consume"),
    GROW("grow"),
    SNOW_GOLEM("snow_golem"),
    SPONGE("sponge"),
    PORTAL("portal"),
    COMMAND("command"),
    PROJECTILE("projectile"),
    VEHICLE("vehicle"),
    EQUIP("equip"),
    ROTATE("rotate"),
    SHEAR("shear"),
    DYE("dye"),
    STATUE("statue"),
    ENDERMAN("enderman"),
    UNKNOWN("unknown");

    private final String source;

    Sources(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return source;
    }
}
