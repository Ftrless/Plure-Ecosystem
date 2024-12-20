package com.enthusiasm.plurelogger.mixin;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actionutils.IHandler;
import com.enthusiasm.plurelogger.actionutils.IHandlerSlot;
import com.enthusiasm.plurelogger.listener.events.ItemInsertEvent;
import com.enthusiasm.plurelogger.listener.events.ItemRemoveEvent;
import com.enthusiasm.plurelogger.utils.ItemData;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements IHandler {
    @Unique
    Map<ItemData, Integer> changedStacks = new HashMap<>();
    @Unique
    private ServerPlayerEntity player = null;

    @Unique
    private BlockPos pos = null;

    @Inject(method = "addSlot", at = @At(value = "HEAD"))
    private void ledgerGiveSlotHandlerReference(Slot slot, CallbackInfoReturnable<Slot> cir) {
        ((IHandlerSlot) slot).setScreenHandler((ScreenHandler) (Object) this);
    }

    @Inject(method = "onButtonClick", at = @At(value = "HEAD"))
    private void ledgerButtonClickGetPlayer(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        this.player = (ServerPlayerEntity) player;
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "HEAD"))
    private void internalOnSlotClickGetPlayer(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        this.player = (ServerPlayerEntity) player;
    }

    @Inject(method = "onSlotClick", at = @At(value = "HEAD"))
    private void ledgerSlotClickGetPlayer(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        this.player = (ServerPlayerEntity) player;
    }

    @Inject(method = "dropInventory", at = @At(value = "HEAD"))
    private void ledgerDropInventoryGetPlayer(PlayerEntity player, Inventory inventory, CallbackInfo ci) {
        this.player = (ServerPlayerEntity) player;
    }

    @Inject(method = "onClosed", at = @At(value = "RETURN"))
    private void ledgerCloseScreenLogChanges(PlayerEntity player, CallbackInfo ci) {
        if (!player.getWorld().isClient) {
            for (var pair : changedStacks.keySet()) {
                ItemStack stack = new ItemStack(Registries.ITEM.getEntry(pair.item()).value(), 1, Optional.of(pair.changes()));

                if (stack.isEmpty()) {
                    continue;
                }
                int count = changedStacks.get(pair);
                int countAbs = Math.abs(count);
                List<ItemStack> splitStacks = new ArrayList<>();

                while (countAbs > 0) {
                    ItemStack addStack = stack.copyWithCount(Math.min(countAbs, stack.getMaxCount()));
                    splitStacks.add(addStack);
                    countAbs -= addStack.getCount();
                }
                if (count > 0) {
                    for (ItemStack splitStack : splitStacks) {
                        ItemInsertEvent.invoke(splitStack, pos, (ServerWorld) player.getWorld(), Sources.PLAYER.getSource(), (ServerPlayerEntity) player);
                    }
                } else {
                    for (ItemStack splitStack : splitStacks) {
                        ItemRemoveEvent.invoke(splitStack, pos, (ServerWorld) player.getWorld(), Sources.PLAYER.getSource(), (ServerPlayerEntity) player);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @Nullable
    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void setPos(@NotNull BlockPos pos) {
        this.pos = pos;
    }

    @Unique
    @Override
    public void onStackChanged(@NotNull ItemStack old, @NotNull ItemStack itemStack, @NotNull BlockPos pos) {
        if (old.isEmpty() && !itemStack.isEmpty()) {
            var key = new ItemData(itemStack.getItem(), itemStack.getOrCreateNbt());

            if (changedStacks.containsKey(key)) {
                changedStacks.put(key, changedStacks.get(key) + itemStack.getCount());
            } else {
                changedStacks.put(key, itemStack.getCount());
            }
        } else if (!old.isEmpty() && itemStack.isEmpty()) {
            var key = new ItemData(old.getItem(), old.getOrCreateNbt());

            if (changedStacks.containsKey(key)) {
                changedStacks.put(key, changedStacks.get(key) - old.getCount());
            } else {
                changedStacks.put(key, -old.getCount());
            }
        } else if (!old.isEmpty() && !itemStack.isEmpty()) {
            onStackChanged(old, ItemStack.EMPTY, pos);
            onStackChanged(ItemStack.EMPTY, itemStack, pos);
        }
    }
}
