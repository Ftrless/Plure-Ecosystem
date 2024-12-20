package com.enthusiasm.plurelogger.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import com.enthusiasm.plurelogger.actionutils.IPlayerCausable;
import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private World world;

    @Shadow
    public abstract @Nullable LivingEntity getCausingEntity();

    @Shadow
    @Nullable
    public Entity entity;

    @Inject(
        method = "affectWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
        )
    )
    private void explosionFireCallback(boolean particles, CallbackInfo ci,
                                             @Local ObjectListIterator<BlockPos> affectedBlocks, @Local BlockPos blockPos) {
        BlockState blockState = AbstractFireBlock.getState(world, blockPos);

        LivingEntity entity;
        if (this.entity instanceof IPlayerCausable playerCausable && playerCausable.getCausablePlayer() != null) {
            entity = playerCausable.getCausablePlayer();
        } else {
            entity = getCausingEntity();
        }

        String source;
        if (this.entity != null && !(this.entity instanceof PlayerEntity)) {
            source = Registries.ENTITY_TYPE.getId(this.entity.getType()).getPath();
        } else {
            source = Sources.EXPLOSION.getSource();
        }

        BlockPlaceEvent.invoke(
                world,
                blockPos,
                blockState,
                world.getBlockEntity(blockPos) != null
                        ? world.getBlockEntity(blockPos)
                        : null,
                source,
                entity instanceof PlayerEntity player
                        ? player
                        : null
        );
    }
}
