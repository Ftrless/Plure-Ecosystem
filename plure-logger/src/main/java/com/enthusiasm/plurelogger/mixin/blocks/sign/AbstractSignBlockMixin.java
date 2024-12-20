package com.enthusiasm.plurelogger.mixin.blocks.sign;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SignChangingItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin {
    @WrapOperation(
            method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/SignChangingItem;useOnSign(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/SignBlockEntity;ZLnet/minecraft/entity/player/PlayerEntity;)Z"
            )
    )
    private boolean logSignItemInteraction(
            SignChangingItem instance,
            World world,
            SignBlockEntity signBlockEntity,
            boolean front,
            PlayerEntity player,
            Operation<Boolean> original
    ) {

        BlockState state = signBlockEntity.getCachedState();
        BlockPos pos = signBlockEntity.getPos();

        @Nullable BlockEntity oldSignEntity = BlockEntity.createFromNbt(pos, state, signBlockEntity.createNbtWithId());

        boolean result = original.call(instance, world, signBlockEntity, front, player);
        if (result && oldSignEntity != null) {
            BlockChangeEvent.invoke(
                    world,
                    pos,
                    state,
                    state,
                    oldSignEntity,
                    signBlockEntity,
                    player
            );
        }

        return result;
    }

}
