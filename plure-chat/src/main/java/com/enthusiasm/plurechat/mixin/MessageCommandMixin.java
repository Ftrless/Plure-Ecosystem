package com.enthusiasm.plurechat.mixin;

import com.enthusiasm.plurechat.event.MessageEvents;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void onExecute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci) {
        ci.cancel();

        MessageEvents.PRIVATE_CHAT_MESSAGE.invoker().onPrivateChatMessage(
                message.getSignedContent(),
                source.getPlayer(),
                targets.stream().findFirst().orElse(null)
        );
    }
}
