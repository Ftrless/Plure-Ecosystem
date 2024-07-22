package com.enthusiasm.plurelogger.mixin;

import com.enthusiasm.plurelogger.event.PlayerConnectCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerConnectCallback.EVENT.invoker().onConnect(player, true);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    public void onPlayerConnect(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerConnectCallback.EVENT.invoker().onConnect(player, false);
    }
}
