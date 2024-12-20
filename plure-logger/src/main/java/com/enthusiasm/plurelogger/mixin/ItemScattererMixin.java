package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actionutils.ILocationalInventory;
import com.enthusiasm.plurelogger.listener.events.ItemRemoveEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ItemScatterer.class)
public abstract class ItemScattererMixin {
    @ModifyArgs(
            method = "spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/inventory/Inventory;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private static void trackContainerBreakRemove(Args args, World world, double x, double y, double z, Inventory inventory) {
        ItemStack stack = inventory.getStack(args.get(0));

        if (!stack.isEmpty() && inventory instanceof ILocationalInventory locationalInventory) {
            ItemRemoveEvent.invoke(stack, locationalInventory.getLocation(), (ServerWorld) world, Sources.BROKE.getSource(), null);
        }
    }
}