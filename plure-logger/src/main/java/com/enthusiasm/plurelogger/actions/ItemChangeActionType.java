package com.enthusiasm.plurelogger.actions;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.utils.*;

public abstract class ItemChangeActionType extends AbstractActionType {
    @Override
    public String getTranslationType() {
        var item = Registries.ITEM.get(getObjectIdentifier());
        if (item instanceof BlockItem && !(item instanceof AliasedBlockItem)) {
            return "block";
        } else {
            return "item";
        }
    }

    private ItemStack getStack(MinecraftServer server) {
        return NbtUtils.itemFromProperties(getExtraData(), getObjectIdentifier(), server.getRegistryManager());
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        var stack = getStack(source.getServer());

        return Text.literal(stack.getCount() + " ").append(
                Text.translatable(Util.createTranslationKey(getTranslationType(), getObjectIdentifier()))
                        .setStyle(TextColorPallet.getSecondaryVariant())
                        .styled(style -> style.withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_ITEM,
                                new HoverEvent.ItemStackContent(stack)
                        )))
        );
    }

    protected void previewItemChange(Preview preview, ServerPlayerEntity player, boolean insert) {
        ServerWorld world = WorldUtils.getWorld(player.getServer(), getWorld());
        var state = world != null ? world.getBlockState(getPos()) : null;

        if (state != null && state.isOf(Blocks.CHEST)) {
            var otherPos = InspectionUtils.getOtherChestSide(state, getPos());
            if (otherPos != null) {
                addPreview(preview, player, otherPos, insert);
            }
        }
        addPreview(preview, player, getPos(), insert);
    }

    private void addPreview(Preview preview, ServerPlayerEntity player, BlockPos pos, boolean insert) {
        preview.getModifiedItems().computeIfAbsent(pos, k -> new java.util.ArrayList<>())
                .add(new Pair<>(getStack(player.getServer()), insert));
    }

    private Inventory getInventory(ServerWorld world) {
        var blockState = world.getBlockState(getPos());
        Block block = blockState.getBlock();

        if (block instanceof InventoryProvider) {
            return ((InventoryProvider) block).getInventory(blockState, world, getPos());
        }

        var blockEntity = world.getBlockEntity(getPos());
        if (blockEntity instanceof Inventory inventory) {
            if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                return ChestBlock.getInventory((ChestBlock) block, blockState, world, getPos(), true);
            }
            return inventory;
        }

        return null;
    }

    protected boolean removeMatchingItem(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());
        var inventory = world != null ? getInventory(world) : null;

        if (inventory != null) {
            var rollbackStack = getStack(server);
            return InventoryUtils.removeMatchingItem(rollbackStack, inventory);
        } else if (world != null) {
            var rollbackStack = getStack(server);
            if (rollbackStack.isOf(Items.WRITABLE_BOOK) || rollbackStack.isOf(Items.WRITTEN_BOOK)) {
                var blockEntity = world.getBlockEntity(getPos());
                if (blockEntity instanceof LecternBlockEntity) {
                    ((LecternBlockEntity) blockEntity).setBook(ItemStack.EMPTY);
                    LecternBlock.setHasBook(null, world, getPos(), blockEntity.getCachedState(), false);
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean addItem(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());
        var inventory = world != null ? getInventory(world) : null;

        if (inventory != null) {
            var rollbackStack = getStack(server);
            return InventoryUtils.addItem(rollbackStack, inventory);
        } else if (world != null) {
            var rollbackStack = getStack(server);
            if (rollbackStack.isOf(Items.WRITABLE_BOOK) || rollbackStack.isOf(Items.WRITTEN_BOOK)) {
                var blockEntity = world.getBlockEntity(getPos());
                if (blockEntity instanceof LecternBlockEntity && !((LecternBlockEntity) blockEntity).hasBook()) {
                    ((LecternBlockEntity) blockEntity).setBook(rollbackStack);
                    LecternBlock.setHasBook(null, world, getPos(), blockEntity.getCachedState(), true);
                    return true;
                }
            }
        }

        return false;
    }
}
