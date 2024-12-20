package com.enthusiasm.plurelogger.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;

import com.enthusiasm.plurelogger.actionutils.IDoubleInventory;

@Mixin(DoubleInventory.class)
public abstract class DoubleInventoryMixin implements IDoubleInventory {
    @Shadow
    @Final
    private Inventory first;

    @Shadow
    @Final
    private Inventory second;

    @NotNull
    @Override
    public Inventory getInventory(int slot) {
        return slot >= this.first.size() ? this.second : this.first;
    }
}
