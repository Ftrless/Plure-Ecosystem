package com.enthusiasm.plureutils.mixin.vanish;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ListCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plureutils.service.VanishService;

@Mixin(ListCommand.class)
public abstract class ListCommandMixin {
    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"
            )
    )
    private static List<ServerPlayerEntity> vanish_removeVanishedPlayers(PlayerManager playerList, ServerCommandSource observer) {
        return VanishService.getVisiblePlayers(observer);
    }
}
