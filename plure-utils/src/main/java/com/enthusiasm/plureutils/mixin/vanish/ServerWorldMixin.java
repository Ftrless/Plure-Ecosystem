package com.enthusiasm.plureutils.mixin.vanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow
    @Nullable
    public abstract Entity getEntityById(int id);

    @WrapOperation(
            method = "setBlockBreakingInfo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    public void vanish_hideBlockDestroyProgress(ServerPlayNetworkHandler packetListener, Packet<?> packet, Operation<Void> original) {
        Entity entity = this.getEntityById(((BlockBreakingProgressS2CPacket) packet).getEntityId());

        if (!(entity instanceof ServerPlayerEntity player) || VanishService.canSeePlayer(player, packetListener.player.getCommandSource())) {
            original.call(packetListener, packet);
        }
    }

    @Inject(
            method = "tickEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tick()V"
            )
    )
    public void vanish_beforeEntityTickNonPassenger(Entity entity, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(entity);
    }

    @Inject(
            method = "tickEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    public void vanish_afterEntityTickNonPassenger(Entity entity, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.remove();
    }

    @Inject(
            method = "tickPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tickRiding()V"
            )
    )
    public void vanish_beforeEntityTick(Entity entity, Entity entity2, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(entity2);
    }

    @Inject(
            method = "tickPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tickRiding()V",
                    shift = At.Shift.AFTER
            )
    )
    public void vanish_afterEntityTick(Entity entity, Entity entity2, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.remove();
    }
}
