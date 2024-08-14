package com.enthusiasm.plureutils.mixin.vanish;

import com.enthusiasm.plureutils.data.vanish.VanishData;
import com.enthusiasm.plureutils.service.VanishService;
import com.enthusiasm.plureutils.util.VanishedEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.playerdata.api.PlayerDataApi;
import me.lucko.fabric.api.permissions.v0.Options;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    private void vanish_vanishOnJoin(ClientConnection connection, ServerPlayerEntity actor, CallbackInfo ci) {
        Boolean vanishOnJoin = Options.get(actor, "vanish_on_join", Boolean::valueOf).orElse(false);

        if (vanishOnJoin) {
            VanishData data = PlayerDataApi.getCustomDataFor(actor.server, actor.getUuid(), VanishService.VANISH_DATA_STORAGE);

            if (data == null) {
                data = new VanishData();
            }

            data.vanished = true;
            PlayerDataApi.setCustomDataFor(actor.server, actor.getUuid(), VanishService.VANISH_DATA_STORAGE, data);

            ((VanishedEntity) actor).markDirty();
        }
    }

    @WrapOperation(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    public void vanish_hideJoinMessage(PlayerManager playerList, Text component, boolean bl, Operation<Void> original, ClientConnection connection, ServerPlayerEntity actor) {
        if (VanishService.isVanished(actor)) {
            //VanishService.broadcastHiddenMessage(actor, component);
        } else {
            original.call(playerList, component, bl);
        }
    }

    @Redirect(
            method = "checkCanJoin",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/PlayerManager;players:Ljava/util/List;"
            )
    )
    private List<ServerPlayerEntity> vanish_getNonVanishedPlayerCount(PlayerManager playerList) {
        return VanishService.getVisiblePlayers(playerList.getServer().getCommandSource().withLevel(0));
    }
}
