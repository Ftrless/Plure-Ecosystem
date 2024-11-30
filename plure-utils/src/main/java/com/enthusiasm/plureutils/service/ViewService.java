package com.enthusiasm.plureutils.service;

import java.io.File;
import java.io.FileOutputStream;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;

import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.command.view.SavingPlayerDataGui;

public class ViewService {
    public static void savePlayerData(ServerPlayerEntity player) {
        File playerDataDir = PlureUtilsEntrypoint.SERVER.getSavePath(WorldSavePath.PLAYERDATA).toFile();
        try {
            NbtCompound compoundTag = player.writeNbt(new NbtCompound());
            File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", playerDataDir);
            final FileOutputStream fos = new FileOutputStream(file);
            NbtIo.writeCompressed(compoundTag, fos);
            File file2 = new File(playerDataDir, player.getUuidAsString() + ".dat");
            File file3 = new File(playerDataDir, player.getUuidAsString() + ".dat_old");
            Util.backupAndReplace(file2.toPath(), file.toPath(), file3.toPath());
        } catch (Exception var6) {
            PlureUtilsEntrypoint.LOGGER.warn("Failed to save player data for {}", player.getName().getString());
        }
    }

    public static void buildAndOpenGui(ServerPlayerEntity senderPlayer, ServerPlayerEntity targetPlayer, Inventory inventory) {
        SimpleGui gui = new SavingPlayerDataGui(ScreenHandlerType.GENERIC_9X5, senderPlayer, targetPlayer);
        gui.setTitle(targetPlayer.getName());

        addBackground(gui);

        for (int i = 0; i < inventory.size(); i++) {
            gui.setSlotRedirect(i, new Slot(inventory, i, 0, 0));
        }

        gui.open();
    }

    private static void addBackground(SimpleGui gui) {
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setSlot(i, new GuiElementBuilder(Items.BARRIER).setName(Text.literal("")).build());
        }
    }
}
