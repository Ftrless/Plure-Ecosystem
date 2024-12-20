package com.enthusiasm.plurelogger.utils;

import java.util.WeakHashMap;

import lombok.Getter;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerLecternHolder {
    @Getter
    private static final WeakHashMap<PlayerEntity, BlockEntity> activeHolders = new WeakHashMap<>();
}
