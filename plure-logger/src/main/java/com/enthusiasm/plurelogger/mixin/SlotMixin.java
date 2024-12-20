package com.enthusiasm.plurelogger.mixin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actionutils.IDoubleInventory;
import com.enthusiasm.plurelogger.actionutils.IHandler;
import com.enthusiasm.plurelogger.actionutils.IHandlerSlot;
import com.enthusiasm.plurelogger.actionutils.ILocationalInventory;

@Mixin(Slot.class)
public abstract class SlotMixin implements IHandlerSlot {
    private ScreenHandler handler = null;
    private ItemStack oldStack = null;

    @Shadow
    @Final
    public Inventory inventory;
    @Shadow
    @Final
    private int index;

    @Shadow
    public abstract ItemStack getStack();

    @NotNull
    @Override
    public ScreenHandler getScreenHandler() {
        return handler;
    }

    @Override
    public void setScreenHandler(@NotNull ScreenHandler handler) {
        this.handler = handler;
        oldStack = this.getStack() == null ? ItemStack.EMPTY : this.getStack().copy();
    }

    @Inject(method = "markDirty", at = @At(value = "HEAD"))
    private void logChanges(CallbackInfo ci) {
        BlockPos pos = getInventoryLocation();
        IHandler handlerWithContext = (IHandler) handler;

        if (pos != null && handlerWithContext.getPlayer() != null) {
            handlerWithContext.onStackChanged(oldStack, this.getStack().copy(), pos);
        }

        oldStack = this.getStack().copy();
    }

    @Unique
    @Nullable
    private BlockPos getInventoryLocation() {
        Inventory slotInventory = this.inventory;

        if (slotInventory instanceof IDoubleInventory) {
            slotInventory = ((IDoubleInventory) slotInventory).getInventory(this.index);
        }

        if (slotInventory instanceof ILocationalInventory) {
            return ((ILocationalInventory) slotInventory).getLocation();
        }

        return null;
    }
}
