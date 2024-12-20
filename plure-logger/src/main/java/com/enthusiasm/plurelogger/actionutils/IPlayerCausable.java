package com.enthusiasm.plurelogger.actionutils;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;

public interface IPlayerCausable {
    @Nullable PlayerEntity causablePlayer = null;
    @Nullable PlayerEntity getCausablePlayer();
}
