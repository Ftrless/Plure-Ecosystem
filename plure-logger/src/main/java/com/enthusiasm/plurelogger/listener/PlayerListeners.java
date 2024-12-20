package com.enthusiasm.plurelogger.listener;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actionutils.ActionFactory;
import com.enthusiasm.plurelogger.listener.events.ItemDropEvent;
import com.enthusiasm.plurelogger.listener.events.ItemPickUpEvent;
import com.enthusiasm.plurelogger.storage.database.maria.ActionQueueService;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.InspectionUtils;

public class PlayerListeners {
    public static void init() {
        UseBlockCallback.EVENT.register(PlayerListeners::onUseBlock);
        AttackBlockCallback.EVENT.register(PlayerListeners::onBlockAttack);
        PlayerBlockBreakEvents.AFTER.register(PlayerListeners::onBlockBreak);
        ServerPlayConnectionEvents.JOIN.register(PlayerListeners::onJoin);
        ItemPickUpEvent.EVENT.register(PlayerListeners::onItemPickUp);
        ItemDropEvent.EVENT.register(PlayerListeners::onItemDrop);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        if (InspectionUtils.isInspecting(player) && hand == Hand.MAIN_HAND) {
            InspectionUtils.inspectBlock(player.getCommandSource(), blockHitResult.getBlockPos().offset(blockHitResult.getSide()));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static ActionResult onBlockAttack(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (InspectionUtils.isInspecting(player)) {
            InspectionUtils.inspectBlock(player.getCommandSource(), pos);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        ActionQueueService.addToQueue(ActionFactory.blockBreakAction(world, pos, state, player, blockEntity));
    }

    private static void onJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        PlayerEntity player = handler.getPlayer();
        DatabaseService.logPlayer(player.getUuid(), player.getEntityName());
    }

    private static void onItemPickUp(ItemEntity entity, PlayerEntity player) {
        ActionQueueService.addToQueue(ActionFactory.itemPickUpAction(entity, player));
    }

    private static void onItemDrop(ItemEntity entity, PlayerEntity player) {
        ActionQueueService.addToQueue(ActionFactory.itemDropAction(entity, player));
    }
}
