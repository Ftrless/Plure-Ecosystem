package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin {
    @ModifyArgs(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void log(Args args, ItemUsageContext context) {
        BlockState state = args.get(1);
        BlockPos pos = args.get(0);
        var player = context.getPlayer();
        var world = context.getWorld();
        var oldState = world.getBlockState(pos);
        var be = world.getBlockEntity(pos);

        if (oldState.getBlock().equals(state.getBlock())) {
            if (player != null) {
                BlockChangeEvent.invoke(context.getWorld(), pos, oldState, state, be, be, player);
            } else {
                BlockChangeEvent.invoke(context.getWorld(), pos, oldState, state, be, be, Sources.FIRE.getSource());
            }
        } else {
            if (player != null) {
                BlockPlaceEvent.invoke(world, pos, state, be, Sources.FIRE.getSource(), player);
            } else {
                BlockPlaceEvent.invoke(world, pos, state, be, Sources.FIRE.getSource());
            }
        }
    }
}
