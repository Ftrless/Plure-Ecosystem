package com.enthusiasm.plurelogger.mixin;

import static net.minecraft.block.JukeboxBlock.HAS_RECORD;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(MusicDiscItem.class)
public abstract class MusicDiscItemMixin {
    @Inject(method = "useOnBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/entity/JukeboxBlockEntity;setStack(Lnet/minecraft/item/ItemStack;)V"))
    public void ledgerPlayerInsertMusicDisc(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);

        BlockChangeEvent.invoke(
                world,
                pos,
                blockState.with(HAS_RECORD, false),
                blockState,
                null,
                world.getBlockEntity(pos),
                Sources.INTERACT.getSource(),
                context.getPlayer()
        );
    }
}