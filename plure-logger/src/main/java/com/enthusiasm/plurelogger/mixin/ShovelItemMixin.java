package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {
    @ModifyArgs(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logPathFlattening(Args args, ItemUsageContext context) {
        var player = context.getPlayer();
        BlockState state = args.get(1);
        BlockPos pos = args.get(0);
        var world = context.getWorld();

        if (player != null) {
            BlockChangeEvent.invoke(world, pos, world.getBlockState(pos), state, null, null, player);
        } else {
            BlockChangeEvent.invoke(world, pos, world.getBlockState(pos), state, null, null, Sources.INTERACT.getSource());
        }
    }
}
