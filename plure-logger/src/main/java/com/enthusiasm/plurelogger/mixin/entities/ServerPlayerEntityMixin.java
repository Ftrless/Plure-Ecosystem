package com.enthusiasm.plurelogger.mixin.entities;

import java.time.Instant;
import java.util.ArrayList;

import lombok.SneakyThrows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurelogger.storage.nbt.NbtService;
import com.enthusiasm.plurelogger.storage.nbt.entity.DeathEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @SneakyThrows
    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeathPlayer(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        DeathEntity deathEntity = new DeathEntity(
                Instant.now(),
                false,
                player.experienceLevel,
                new DeathEntity.Location(
                        (int) player.getX(),
                        (int) player.getY(),
                        (int) player.getZ(),
                        player.getServerWorld().getRegistryKey().getValue().toString()
                ),
                damageSource.getDeathMessage(player).getString(),
                player.getInventory().main,
                player.getInventory().armor,
                new ArrayList<>()
        );

        NbtService.saveDeathLog(player.getEntityName(), deathEntity);
    }
}
