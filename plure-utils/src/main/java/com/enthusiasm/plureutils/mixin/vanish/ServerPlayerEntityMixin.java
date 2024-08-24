package com.enthusiasm.plureutils.mixin.vanish;

import com.mojang.authlib.GameProfile;
import eu.pb4.playerdata.api.PlayerDataApi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plureutils.data.vanish.VanishData;
import com.enthusiasm.plureutils.service.VanishService;
import com.enthusiasm.plureutils.util.VanishedEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements VanishedEntity {
    @Shadow @Final public MinecraftServer server;

    @Unique private boolean dirty = true;
    @Unique private boolean vanished;

    public ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Override
    public boolean isVanished() {
        if (dirty) {
            VanishData data = PlayerDataApi.getCustomDataFor(server, uuid, VanishService.VANISH_DATA_STORAGE);
            vanished = data != null && data.vanished;
            dirty = false;
        }

        return vanished;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }
}
