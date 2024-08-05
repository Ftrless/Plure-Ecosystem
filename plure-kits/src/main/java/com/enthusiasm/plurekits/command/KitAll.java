package com.enthusiasm.plurekits.command;

import com.enthusiasm.plurekits.KitService;
import com.enthusiasm.plurekits.data.DataManager;
import com.enthusiasm.plurekits.data.kit.KitData;
import com.enthusiasm.plurekits.data.player.PlayerKitData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SimpleGuiBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class KitAll implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context, context.getSource().getPlayer());

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) {
        PlayerKitData playerKitData = DataManager.getInstance().getPlayerKitData(senderPlayer);
        Stream<Map.Entry<String, KitData>> allPlayerKits = KitService.getAllKitsForPlayer(senderPlayer);
        long currentTime = Util.getEpochTimeMs();

        Function<Map.Entry<String, KitData>, Boolean> canUseKit = (entry) ->
                (playerKitData.getKitUsedTime(entry.getKey()) + entry.getValue().getCooldown()) - currentTime <= 0;

        SimpleGuiBuilder guiBuilder = new SimpleGuiBuilder(ScreenHandlerType.GENERIC_9X6, false);
        guiBuilder.setLockPlayerInventory(true);
        guiBuilder.setTitle(Text.literal("Взять кит"));

        int i = 0;
        for (var kitEntry : allPlayerKits.toList()) {
            ItemStack defaultItemStack = (canUseKit.apply(kitEntry)
                    ? kitEntry.getValue()
                    .getDisplayItem()
                    .orElse(Items.EMERALD_BLOCK)
                    : Items.GRAY_CONCRETE_POWDER)
                    .getDefaultStack();

            guiBuilder.setSlot(
                    i++,
                    defaultItemStack
                            .copy()
                            .setCustomName(Text.literal(kitEntry.getKey())),
                    (index, type, action, gui) -> {
                        if (type.isLeft) {
                            try {
                                KitClaim.exec(context, senderPlayer, kitEntry.getKey());
                            } catch (CommandSyntaxException ignore) {}
                            gui.close();
                        }
                    });
        }

        SimpleGui simpleGui = guiBuilder.build(senderPlayer);
        simpleGui.open();
    }
}
