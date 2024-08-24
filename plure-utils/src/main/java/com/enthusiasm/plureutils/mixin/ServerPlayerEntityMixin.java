package com.enthusiasm.plureutils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import com.enthusiasm.plureutils.PermissionsHolder;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(
            method = "copyFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendAbilitiesUpdate()V")
    )
    private void keepInventory(ServerPlayerEntity oldPlayer, boolean pKeepEverything, CallbackInfo ci) {
        ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;
        newPlayer.sendAbilitiesUpdate();

        if (PermissionsHolder.check(oldPlayer, PermissionsHolder.Permission.KEEP_INV, 4) && !pKeepEverything) {
            newPlayer.getInventory().clone(oldPlayer.getInventory());
            newPlayer.setHealth(20);
            newPlayer.experienceLevel = oldPlayer.experienceLevel;
            newPlayer.totalExperience = oldPlayer.totalExperience;
            newPlayer.experienceProgress = oldPlayer.experienceProgress;
            newPlayer.setScore(oldPlayer.getScore());
        }

        newPlayer.sendAbilitiesUpdate();
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;"))
    void teleportFix(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        for (StatusEffectInstance statusEffectInstance : self.getStatusEffects())
            self.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(self.getId(), statusEffectInstance));

        self.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(self.experienceProgress, self.totalExperience, self.experienceLevel));
    }
}
