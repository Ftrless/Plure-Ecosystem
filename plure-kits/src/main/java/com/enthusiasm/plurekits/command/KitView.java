package com.enthusiasm.plurekits.command;

import com.enthusiasm.plurekits.KitService;
import com.enthusiasm.plurekits.data.kit.KitData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGuiBuilder;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicInteger;

public class KitView implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String kitName = StringArgumentType.getString(context, "kit_name");

        exec(
                context,
                context.getSource().getPlayer(),
                kitName
        );

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, String kitName) {
        KitData kitData = KitService.getKit(kitName);

        var guiBuilder = new SimpleGuiBuilder(ScreenHandlerType.GENERIC_9X4, false);
        guiBuilder.setLockPlayerInventory(true);
        guiBuilder.setTitle(Text.literal("Содержимое кита " + kitName));

        AtomicInteger i = new AtomicInteger();
        kitData.getInventory().main.forEach(itemStack -> {
            guiBuilder.setSlot(
                    i.getAndIncrement(),
                    itemStack,
                    (index, type, action, gui) -> {}
            );
        });

        var simpleGui = guiBuilder.build(senderPlayer);
        simpleGui.open();
    }
}
