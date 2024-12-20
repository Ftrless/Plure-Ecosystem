package com.enthusiasm.plurelogger.actionutils;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface IHandler {
    @Nullable ServerPlayerEntity getPlayer();
    BlockPos getPos();
    void setPos(BlockPos pos);
    void onStackChanged(ItemStack oldStack, ItemStack newStack, BlockPos pos);
}
