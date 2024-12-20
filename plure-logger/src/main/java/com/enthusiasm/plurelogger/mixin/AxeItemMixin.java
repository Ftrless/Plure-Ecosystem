package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
    @ModifyArgs(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logAxeUsage(Args args, ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState oldState = world.getBlockState(pos);
        BlockState newState = args.get(1);
        PlayerEntity player = context.getPlayer();

        if (player != null) {
            BlockChangeEvent.invoke(world, pos, oldState, newState, null, null, player);
        } else {
            BlockChangeEvent.invoke(world, pos, oldState, newState, null, null, Sources.INTERACT.getSource());
        }
    }
}