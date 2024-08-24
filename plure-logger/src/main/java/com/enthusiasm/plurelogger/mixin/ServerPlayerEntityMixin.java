package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurelogger.event.PlayerDeathCallback;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeathPlayer(DamageSource damageSource, CallbackInfo ci) {
        PlayerDeathCallback.EVENT.invoker().onDeath((ServerPlayerEntity) (Object) this, damageSource);
    }
}
