package com.enthusiasm.plureutils.mixin.vanish;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract void sendPacket(Packet<?> packet);

    @Inject(
            method = "sendPacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void vanish_modifyPackets(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayNetworkHandler listener) {
            if (packet instanceof ItemPickupAnimationS2CPacket takeItemEntityPacket) {
                Entity entity = listener.player.getWorld().getEntityById(takeItemEntityPacket.getEntityId());

                if (entity instanceof ServerPlayerEntity actor && !VanishService.canSeePlayer(actor, listener.player.getCommandSource())) {
                    this.sendPacket(new EntitiesDestroyS2CPacket(takeItemEntityPacket.getEntityId()));
                    ci.cancel();
                }
            } else if (packet instanceof PlayerListS2CPacket playerInfoPacket) {
                ObjectArrayList<ServerPlayerEntity> modifiedEntries = new ObjectArrayList<>();
                int visible = 0;

                for (PlayerListS2CPacket.Entry playerUpdate : playerInfoPacket.getEntries()) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUpdate.profileId());

                    if (VanishService.canSeePlayer(player, listener.player.getCommandSource())) {
                        visible++;
                        if (player != null) {
                            modifiedEntries.add(player);
                        }
                    }
                }

                if (visible != playerInfoPacket.getEntries().size()) {
                    if (!modifiedEntries.isEmpty()) {
                        this.sendPacket(new PlayerListS2CPacket(playerInfoPacket.getActions(), modifiedEntries));
                    }

                    ci.cancel();
                }
            }
        }
    }
}
