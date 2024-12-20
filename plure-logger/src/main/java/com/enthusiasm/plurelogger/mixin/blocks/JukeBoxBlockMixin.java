package com.enthusiasm.plurelogger.mixin.blocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(JukeboxBlock.class)
public abstract class JukeBoxBlockMixin {
    @Shadow
    @Final
    public static BooleanProperty HAS_RECORD;

    @Inject(method = "onUse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/entity/JukeboxBlockEntity;dropRecord()V"))
    private void logDiscRemoved(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand,
                                      BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        BlockChangeEvent.invoke(
                world,
                pos,
                blockState,
                blockState.with(HAS_RECORD, false),
                world.getBlockEntity(pos),
                null,
                Sources.INTERACT.getSource(),
                player
        );
    }
}
