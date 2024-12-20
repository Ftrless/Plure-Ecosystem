package com.enthusiasm.plurelogger.mixin.blocks.sign;

import java.util.UUID;
import java.util.function.UnaryOperator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {
    @WrapOperation(
            method = "tryChangeText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/SignBlockEntity;changeText(Ljava/util/function/UnaryOperator;Z)Z"
            )
    )
    private boolean logSignTextChange(
            SignBlockEntity instance,
            UnaryOperator<SignText> textChanger,
            boolean front,
            Operation<Boolean> original
    ) {

        BlockPos pos = instance.getPos();
        BlockState state = instance.getCachedState();

        @Nullable BlockEntity oldSignEntity = BlockEntity.createFromNbt(pos, state, instance.createNbtWithId());

        boolean result = original.call(instance, textChanger, front);
        if (result && oldSignEntity != null) {

            World world = instance.getWorld();
            assert world != null : "World cannot be null, this is already in the target method";

            UUID editorID = instance.getEditor();
            PlayerEntity player = world.getPlayerByUuid(editorID);
            assert player != null : "The editor must exist, this is already checked in target method";

            BlockChangeEvent.invoke(
                    world,
                    pos,
                    state,
                    state,
                    oldSignEntity,
                    instance,
                    player
            );
        }

        return result;
    }

}
