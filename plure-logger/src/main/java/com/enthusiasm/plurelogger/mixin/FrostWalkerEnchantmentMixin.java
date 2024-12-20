package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(FrostWalkerEnchantment.class)
public abstract class FrostWalkerEnchantmentMixin {
    @ModifyArgs(method = "freezeWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    private static void logFrostWalkerPlacement(Args args, LivingEntity entity, World world, BlockPos entityPos, int level) {
        BlockPos pos = args.get(0);
        pos = pos.toImmutable();
        if (!(world.getBlockState(pos).getBlock() == Blocks.FROSTED_ICE)) {
            BlockPlaceEvent.invoke(
                    world,
                    pos,
                    Blocks.FROSTED_ICE.getDefaultState(),
                    null,
                    Sources.FROST_WALKER.getSource(),
                    entity instanceof PlayerEntity
                            ? (PlayerEntity) entity
                            : null
            );
        }
    }
}
