package com.enthusiasm.plurekits.command;

import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.TimeUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plurekits.KitService;
import com.enthusiasm.plurekits.PlureKitsEntrypoint;
import com.enthusiasm.plurekits.data.kit.KitData;
import com.enthusiasm.plurekits.data.kit.KitInventoryData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;

public class KitAdd implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String kitName = StringArgumentType.getString(context, "kit_name");
        String cooldown = StringArgumentType.getString(context, "cooldown");
        String needsPlayed = StringArgumentType.getString(context, "needs_played");

        exec(
                context,
                context.getSource().getPlayerOrThrow(),
                kitName,
                TimeUtils.parseDuration(cooldown),
                TimeUtils.parseDuration(needsPlayed)
        );

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, String kitName, long cooldown, long needsPlayed) {
        var kitInventory = new KitInventoryData();
        kitInventory.copyFrom(senderPlayer.getInventory());

        KitData kitData = new KitData(kitInventory, cooldown, needsPlayed);

        try {
            NbtCompound kitNbt = new NbtCompound();
            kitData.writeNBT(kitNbt);

            NbtIo.write(
                    kitNbt,
                    KitService.getKitsDir().toPath().resolve(String.format("%s.nbt", kitName)).toFile()
            );
        } catch (IOException e) {
            PlureKitsEntrypoint.LOGGER.error("Ошибка сохранения кита: {}", e.getMessage());
        }

        PlayerUtils.sendFeedback(context, "cmd.kit.add.feedback", kitName);
    }
}
