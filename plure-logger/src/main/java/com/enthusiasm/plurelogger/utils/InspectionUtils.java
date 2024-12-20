package com.enthusiasm.plurelogger.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.CacheService;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.actionutils.SearchResults;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;

public class InspectionUtils {
    private static final Set<UUID> inspectingUsers = new HashSet<>();
    private static final Set<UUID> searchingUsers = new HashSet<>();

    public static boolean isInspecting(PlayerEntity player) {
        return inspectingUsers.contains(player.getUuid());
    }

    public static int inspectOn(PlayerEntity player) {
        inspectingUsers.add(player.getUuid());
        player.sendMessage(
                Text.translatable("text.inspect.toggle", Text.translatable("text.inspect.on")
                                .formatted(Formatting.GREEN))
                        .setStyle(TextColorPallet.getSecondary()),
                false
        );

        return 1;
    }

    public static int inspectOff(PlayerEntity player) {
        inspectingUsers.remove(player.getUuid());
        player.sendMessage(
                Text.translatable("text.inspect.toggle", Text.translatable("text.inspect.off")
                                .formatted(Formatting.RED))
                        .setStyle(TextColorPallet.getSecondary()),
                false
        );

        return 1;
    }

    public static void inspectBlock(ServerCommandSource source, BlockPos pos) {
        if (searchingUsers.contains(source.getPlayer().getUuid())) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            BlockBox area = new BlockBox(pos);

            BlockState state = source.getWorld().getBlockState(pos);
            if (state.isOf(Blocks.CHEST)) {
                BlockPos otherPos = getOtherChestSide(state, pos);
                if (otherPos != null) {
                    area = BlockBox.create(pos, otherPos);
                }
            } else if (state.getBlock() instanceof DoorBlock) {
                BlockPos otherPos = getOtherDoorHalf(state, pos);
                area = BlockBox.create(pos, otherPos);
            } else if (state.getBlock() instanceof BedBlock) {
                BlockPos otherPos = getOtherBedPart(state, pos);
                area = BlockBox.create(pos, otherPos);
            }

            ActionSearchParams params = ActionSearchParams.build()
                    .setBounds(area)
                    .setWorlds(Set.of(Negatable.allow(source.getWorld().getRegistryKey().getValue())))
                    .build();

            CacheService.getSearchCache().put(source.getName(), params);
            searchingUsers.add(source.getEntity().getUuid());
            //TODO: warn busy

            SearchResults results = DatabaseService.searchActions(params, 1);
            searchingUsers.remove(source.getEntity().getUuid());

            if (results.actions().isEmpty()) {
                source.sendError(Text.translatable("error.command.no_results"));
                return;
            }

            MessageUtils.sendSearchResults(
                    source,
                    results,
                    Text.translatable(
                            "text.header.search.pos",
                            Text.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ())
                    ).setStyle(TextColorPallet.getPrimary())
            );
        }, ThreadUtils.getAsyncExecutor());
    }

    public static BlockPos getOtherChestSide(BlockState state, BlockPos pos) {
        ChestType type = state.get(ChestBlock.CHEST_TYPE);
        if (type != ChestType.SINGLE) {
            Direction facing = state.get(ChestBlock.FACING);
            return (type == ChestType.RIGHT)
                    ? pos.offset(facing.rotateCounterclockwise(Direction.Axis.Y))
                    : pos.offset(facing.rotateClockwise(Direction.Axis.Y));
        }
        return null;
    }

    public static BlockPos getOtherDoorHalf(BlockState state, BlockPos pos) {
        DoubleBlockHalf half = state.get(DoorBlock.HALF);
        return (half == DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
    }

    public static BlockPos getOtherBedPart(BlockState state, BlockPos pos) {
        BedPart part = state.get(BedBlock.PART);
        Direction direction = state.get(BedBlock.FACING);
        return (part == BedPart.FOOT) ? pos.offset(direction) : pos.offset(direction.getOpposite());
    }

    public static CompletableFuture<SearchResults> getInspectResults(PlayerEntity player, BlockPos pos) {
        return CompletableFuture.supplyAsync(() -> {
            ServerCommandSource source = player.getCommandSource();
            ActionSearchParams params = ActionSearchParams.build()
                    .setBounds(new BlockBox(pos))
                    .build();

            CacheService.getSearchCache().put(source.getName(), params);
            //TODO: warn busy

            return DatabaseService.searchActions(params, 1);
        }, ThreadUtils.getAsyncExecutor());
    }
}
