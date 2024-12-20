package com.enthusiasm.plurelogger.listener;

import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.actionutils.ActionFactory;
import com.enthusiasm.plurelogger.listener.events.BlockBreakEvent;
import com.enthusiasm.plurelogger.listener.events.BlockChangeEvent;
import com.enthusiasm.plurelogger.listener.events.BlockMeltEvent;
import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;
import com.enthusiasm.plurelogger.utils.Sources;

public class BlockListeners {
    public static void init() {
        BlockPlaceEvent.EVENT.register(BlockListeners::onBlockPlace);
        BlockBreakEvent.EVENT.register(BlockListeners::onBlockBreak);
        BlockChangeEvent.EVENT.register(BlockListeners::onBlockChange);
        BlockMeltEvent.EVENT.register(BlockListeners::onMelt);
    }

    private static void onBlockPlace(World world, BlockPos pos, BlockState state, BlockEntity entity, String source, PlayerEntity player) {
        IActionType action;

        if (player != null) {
            action = ActionFactory.blockPlaceAction(world, pos, state, player, entity, source);
        } else {
            action = ActionFactory.blockPlaceAction(world, pos, state, source, entity);
        }

        ActionQueueService.addToQueue(action);
    }

    private static void onBlockBreak(World world, BlockPos pos, BlockState state, BlockEntity entity, String source, PlayerEntity player) {
        IActionType action;

        if (player != null) {
            action = ActionFactory.blockBreakAction(world, pos, state, player, entity, source);
        } else {
            action = ActionFactory.blockBreakAction(world, pos, state, source, entity);
        }

        ActionQueueService.addToQueue(action);
    }

    private static void onBlockChange(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity oldBlockEntity, BlockEntity newBlockEntity, String source, PlayerEntity player) {
        ActionQueueService.addToQueue(
                ActionFactory.blockChangeAction(world, pos, oldState, newState, oldBlockEntity, source, player)
        );
    }

    private static void onMelt(World world, BlockPos pos, BlockState oldState, BlockState newState, BlockEntity entity) {
        ActionQueueService.addToQueue(
                ActionFactory.blockBreakAction(world, pos, oldState, Sources.MELT.getSource(), entity)
        );

        if (!(newState.getBlock() instanceof AirBlock)) {
            ActionQueueService.addToQueue(
                    ActionFactory.blockPlaceAction(world, pos, newState, Sources.MELT.getSource(), entity)
            );
        }
    }
}
