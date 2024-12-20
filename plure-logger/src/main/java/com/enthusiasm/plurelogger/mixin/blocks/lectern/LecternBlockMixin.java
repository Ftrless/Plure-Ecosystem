package com.enthusiasm.plurelogger.mixin.blocks.lectern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.listener.events.ItemInsertEvent;
import com.enthusiasm.plurelogger.utils.PlayerLecternHolder;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {
    @Inject(method = "putBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private static void logPutBook(Entity user, World world, BlockPos pos, BlockState state, ItemStack stack, CallbackInfo ci) {
        LecternBlockEntity blockEntity = (LecternBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return;
        ItemInsertEvent.invoke(blockEntity.getBook(), pos, (ServerWorld) world, Sources.PLAYER.getSource(), (ServerPlayerEntity) user);
    }

    @Inject(method = "openScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;"))
    public void storeLectern(World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
        PlayerLecternHolder.getActiveHolders().put(player, world.getBlockEntity(pos));
    }
}
