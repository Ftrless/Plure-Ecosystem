package com.enthusiasm.plureutils.mixin;

import com.enthusiasm.plureutils.PermissionsHolder;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedPlayerManager.class)
public abstract class DedicatedPlayerManagerMixin {
    @Shadow public abstract MinecraftDedicatedServer getServer();

    @Inject(method = "canBypassPlayerLimit", at = @At("HEAD"), cancellable = true)
    public void canBypassPlayerLimit(GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
        PermissionsHolder.check(profile, PermissionsHolder.Permission.BYPASS_PLAYER_LIMIT, 4, getServer())
                .thenAcceptAsync(cir::setReturnValue);
    }
}
