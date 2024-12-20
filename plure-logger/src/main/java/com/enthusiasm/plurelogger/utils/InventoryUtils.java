package com.enthusiasm.plurelogger.utils;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static boolean addItem(ItemStack rollbackStack, Inventory inventory) {
        // Check if the inventory has enough space
        int matchingCountLeft = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                matchingCountLeft += rollbackStack.getMaxCount();
            } else if (ItemStack.areItemsEqual(stack, rollbackStack) && ItemStack.areEqual(stack, rollbackStack)) {
                matchingCountLeft += stack.getMaxCount() - stack.getCount();
            }
        }
        if (matchingCountLeft < rollbackStack.getCount()) {
            return false;
        }

        int requiredCount = rollbackStack.getCount();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                if (requiredCount > rollbackStack.getMaxCount()) {
                    inventory.setStack(i, rollbackStack.copyWithCount(rollbackStack.getMaxCount()));
                    requiredCount -= rollbackStack.getMaxCount();
                } else {
                    inventory.setStack(i, rollbackStack.copyWithCount(requiredCount));
                    requiredCount = 0;
                }
            } else if (ItemStack.areItemsEqual(stack, rollbackStack) && ItemStack.areEqual(stack, rollbackStack)) {
                int countUntilMax = rollbackStack.getMaxCount() - stack.getCount();
                if (requiredCount > countUntilMax) {
                    inventory.setStack(i, rollbackStack.copyWithCount(rollbackStack.getMaxCount()));
                    requiredCount -= countUntilMax;
                } else {
                    stack.increment(requiredCount); // Adds the requiredCount to the current stack
                    requiredCount = 0;
                }
            }
            if (requiredCount <= 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeMatchingItem(ItemStack rollbackStack, Inventory inventory) {
        // Check if the inventory has enough matching items
        int matchingCount = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (ItemStack.areItemsEqual(stack, rollbackStack) && ItemStack.areEqual(stack, rollbackStack)) {
                matchingCount += stack.getCount();
            }
        }
        if (matchingCount < rollbackStack.getCount()) {
            return false;
        }

        int requiredCount = rollbackStack.getCount();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (ItemStack.areItemsEqual(stack, rollbackStack) && ItemStack.areEqual(stack, rollbackStack)) {
                if (requiredCount < stack.getCount()) {
                    // Only some parts of this stack are needed
                    stack.decrement(requiredCount); // Reduces the stack by requiredCount
                    return true;
                } else {
                    inventory.setStack(i, ItemStack.EMPTY);
                    requiredCount -= stack.getCount();
                }
                if (requiredCount <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
