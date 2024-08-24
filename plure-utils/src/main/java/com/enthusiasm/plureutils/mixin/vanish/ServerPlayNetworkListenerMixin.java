package com.enthusiasm.plureutils.mixin.vanish;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkListenerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(
            method = "onPlayerAction",
            at = @At(
                    value = "INVOKE",
            target = "Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;getPos()Lnet/minecraft/util/math/BlockPos;"
    )
    )
    public void vanish_beforeHandlePlayerAction(PlayerActionC2SPacket serverboundPlayerActionPacket, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(this.player);
    }

    @Inject(
            method = "onPlayerInteractBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;updateSequence(I)V"
            )
    )
    public void vanish_beforeHandleUseItemOn(PlayerInteractBlockC2SPacket serverboundUseItemOnPacket, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(this.player);
    }

    @Inject(
            method = "onPlayerInteractItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;updateSequence(I)V"
            )
    )
    public void vanish_beforeHandleUseItem(PlayerInteractItemC2SPacket serverboundUseItemPacket, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(this.player);
    }

    @Inject(
            method = "onPlayerInteractEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;",
                    ordinal = 1
            )
    )
    public void vanish_beforeHandleInteract(PlayerInteractEntityC2SPacket serverboundInteractPacket, CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(this.player);
    }

    @Inject(
            method = {"onPlayerAction", "onPlayerInteractBlock", "onPlayerInteractItem", "onPlayerInteractEntity"},
            at = @At("RETURN")
    )
    public void vanish_afterPacket(CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.remove();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void vanish_beforeTick(CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.set(this.player);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void vanish_afterTick(CallbackInfo ci) {
        VanishService.ACTIVE_ENTITY.remove();
    }
}
