package com.enthusiasm.plureutils.mixin.vanish;

import com.enthusiasm.plureutils.service.VanishService;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract ServerCommandSource getCommandSource();

    @ModifyReceiver(
            method = "createMetadataPlayers",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;size()I"
            )
    )
    public List<ServerPlayerEntity> vanish_getNonVanishedPlayerCount(List<ServerPlayerEntity> original) {
        return VanishService.getVisiblePlayers(this.getCommandSource().withLevel(0));
    }

    @ModifyReceiver(
            method = "createMetadataPlayers",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;"
            )
    )
    public List<ServerPlayerEntity> vanish_getNonVanishedPlayer(List<ServerPlayerEntity> original, int index) {
        return VanishService.getVisiblePlayers(this.getCommandSource().withLevel(0));
    }

    /**
     * @author Enthusiasm
     * @reason Hide vanished players in server queries, fail fast
     */
    @Overwrite
    public int getCurrentPlayerCount() {
        return VanishService.getVisiblePlayers(this.getCommandSource().withLevel(0)).size();
    }

}
