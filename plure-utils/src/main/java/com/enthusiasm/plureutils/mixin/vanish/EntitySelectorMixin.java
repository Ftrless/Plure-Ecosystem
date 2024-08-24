package com.enthusiasm.plureutils.mixin.vanish;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorMixin {
    @Inject(method = "getPlayers", at = @At("RETURN"))
    public void vanish_removeVanishedPlayers(ServerCommandSource src, CallbackInfoReturnable<List<ServerPlayerEntity>> cir) {
        List<ServerPlayerEntity> players = cir.getReturnValue();
        ServerPlayerEntity observer = src.getPlayer();

        if (observer != null) {
            players.removeIf((actor) -> !VanishService.canSeePlayer(actor, observer.getCommandSource()));
        }
    }
}
