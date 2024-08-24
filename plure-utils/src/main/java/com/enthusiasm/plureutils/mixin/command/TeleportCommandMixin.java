package com.enthusiasm.plureutils.mixin.command;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.server.command.TeleportCommand;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @ModifyArg(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;"), index = 0, require = 0)
    private static String pu_renameCommand(String def) {
        if (def.equals("teleport")) return "minecraft:teleport";
        if (def.equals("tp")) return "minecraft:tp";

        return def;
    }
}
