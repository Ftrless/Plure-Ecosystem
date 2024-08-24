package com.enthusiasm.plurechat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurechat.api.MessageEventsAPI;
import com.enthusiasm.plurechat.event.MessageEvents;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow private static boolean hasIllegalCharacter(String message) {
        return false;
    }
    @Shadow public abstract ServerPlayerEntity getPlayer();
    @Shadow protected abstract void checkForSpam();

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        String message = packet.chatMessage();

        if (hasIllegalCharacter(message)) {
            ci.cancel();
            return;
        }

        MessageEvents.CHAT_MESSAGE.invoker().onChatMessage(message, getPlayer());
        checkForSpam();

        ci.cancel();
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"))
    private void onCommand(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        MessageEventsAPI.COMMAND_EXECUTE.invoker().onCommandExecute(packet.command(), getPlayer());
    }
}
