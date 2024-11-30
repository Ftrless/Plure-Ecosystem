package com.enthusiasm.plureutils.command.view;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plureutils.service.ViewService;

public class ViewEChest implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = PlayerUtils.getPlayer(context);

        if (targetPlayer != null) {
            exec(context, senderPlayer, targetPlayer);
        }

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, ServerPlayerEntity targetPlayer) {
        EnderChestInventory requestedEchest = targetPlayer.getEnderChestInventory();

        ViewService.buildAndOpenGui(senderPlayer, targetPlayer, requestedEchest);
    }
}
