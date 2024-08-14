package com.enthusiasm.plureutils.mixin.vanish;

import com.enthusiasm.plureutils.service.VanishService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "canBeSpectated", at = @At("HEAD"), cancellable = true)
    public void vanish_shouldBroadcast(ServerPlayerEntity observer, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity actor = getActor();

        if (actor == null) {
            return;
        }

        if (!VanishService.canSeePlayer(actor, observer.getCommandSource())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void vanish_markRenderInvisible(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        if (self instanceof ServerPlayerEntity actor && player instanceof ServerPlayerEntity observer && !VanishService.canSeePlayer(actor, observer.getCommandSource())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void vanish_preventSound(SoundEvent soundEvent, float f, float g, CallbackInfo ci) {
        ServerPlayerEntity actor = getActor();

        if (actor == null) {
            return;
        }

        if (VanishService.isVanished(actor)) {
            ci.cancel();
        }
    }

    @Unique
    private ServerPlayerEntity getActor() {
        Entity self = (Entity) (Object) this;
        ServerPlayerEntity actor;

        if (self instanceof ServerPlayerEntity player) {
            actor = player;
        } else if (self instanceof Ownable traceableEntity && traceableEntity.getOwner() instanceof ServerPlayerEntity owner) {
            actor = owner;
        } else {
            actor = null;
        }

        return actor;
    }
}
