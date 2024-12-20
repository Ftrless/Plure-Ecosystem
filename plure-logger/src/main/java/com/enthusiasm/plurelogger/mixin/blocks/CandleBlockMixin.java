package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(AbstractCandleBlock.class)
public abstract class CandleBlockMixin {
    @Shadow
    @Final
    public static BooleanProperty LIT;

    @Inject(method = "extinguish", at = @At(value = "RETURN"))
    private static void logCandleExtinguish(PlayerEntity player, BlockState state, WorldAccess worldAccess, BlockPos pos, CallbackInfo ci) {
        if (worldAccess instanceof World world) {
            BlockChangeEvent.invoke(
                    world,
                    pos,
                    state,
                    state.with(LIT, !state.get(LIT)),
                    null,
                    null,
                    Sources.EXTINGUISH.getSource(),
                    player
            );
        }
    }

    @Inject(method = "onProjectileHit", at = @At(value = "RETURN"))
    private void logCandleLit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile, CallbackInfo ci) {
        BlockChangeEvent.invoke(
                world,
                hit.getBlockPos(),
                state,
                state.with(LIT, !state.get(LIT)),
                null,
                null,
                Sources.FIRE.getSource()
        );
    }
}
