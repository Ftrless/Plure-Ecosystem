package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @Unique
    private static BlockState newBlockState;

    @Inject(method = "method_24922",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private static void ledgerLogAnvilBreak(PlayerEntity player, World world, BlockPos pos, CallbackInfo ci) {
        BlockBreakEvent.invoke(world, pos, world.getBlockState(pos), null, Sources.DECAY.getSource(), player);
    }

    @ModifyArgs(method = "method_24922",
               at = @At(value = "INVOKE",
               target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private static void logAnvilChange(Args args, PlayerEntity player, World world, BlockPos pos) {
        BlockState newBlockState = args.get(1);
        BlockChangeEvent.invoke(world, pos, world.getBlockState(pos), newBlockState, world.getBlockEntity(pos), null, Sources.DECAY.getSource(), player);
    }
}
