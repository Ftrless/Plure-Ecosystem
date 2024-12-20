package com.enthusiasm.plurelogger.actionutils;

import java.util.*;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.command.commands.*;
import com.enthusiasm.plurelogger.mixin.preview.EntityTrackerEntryAccessor;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public class Preview {
    private final ActionSearchParams params;
    @Getter
    private final Set<BlockPos> positions = new HashSet<>();
    @Getter
    private final Set<EntityTrackerEntry> spawnedEntityTrackers = new HashSet<>();
    @Getter
    private final Set<EntityTrackerEntry> removedEntityTrackers = new HashSet<>();
    @Getter
    private final Map<BlockPos, List<Pair<ItemStack, Boolean>>> modifiedItems = new HashMap<>();
    private final Type type;

    public Preview(ActionSearchParams params, List<IActionType> actions, ServerPlayerEntity player, Type type) {
        this.params = params;
        this.type = type;

        player.sendMessage(
                Text.translatable("preview.start", actions.size())
                        .setStyle(TextColorPallet.getPrimary()),
                false
        );

        for (IActionType action : actions) {
            switch (type) {
                case ROLLBACK -> action.previewRollback(this, player);
                case RESTORE -> action.previewRestore(this, player);
            }
        }
    }

    public void cancel(ServerPlayerEntity player) {
        for (BlockPos pos : positions) {
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(player.getWorld(), pos));
        }
        cleanup(player);
    }

    private void cleanup(ServerPlayerEntity player) {
        for (EntityTrackerEntry entry : spawnedEntityTrackers) {
            if (!isEntityPresent(entry)) {
                entry.stopTracking(player);
            }
        }

        for (EntityTrackerEntry entry : removedEntityTrackers) {
            if (isEntityPresent(entry)) {
                entry.startTracking(player);
            }
        }
    }

    private boolean isEntityPresent(EntityTrackerEntry entityTrackerEntry) {
        return ((EntityTrackerEntryAccessor) entityTrackerEntry).getEntity()
                .getWorld().getEntityById(((EntityTrackerEntryAccessor) entityTrackerEntry).getEntity().getId()) != null;
    }

    public void apply(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        cleanup(context.getSource().getPlayerOrThrow());
        switch (type) {
            case ROLLBACK:
                new RollbackCommand().rollback(context, params);
                break;
            case RESTORE:
                new RestoreCommand().restore(context, params);
                break;
        }
    }

    public enum Type {
        ROLLBACK,
        RESTORE
    }
}