package com.enthusiasm.plurelogger.actions;

import java.util.Optional;

import lombok.SneakyThrows;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.utils.Logger;
import com.enthusiasm.plurelogger.utils.NbtUtils;
import com.enthusiasm.plurelogger.utils.TextColorPallet;
import com.enthusiasm.plurelogger.utils.WorldUtils;

public class BlockChangeActionType extends AbstractActionType {
    @Override
    public String getIdentifier() {
        return "block-change";
    }

    @SneakyThrows
    @Override
    public boolean rollback(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        if (world != null) {
            BlockPos pos = getPos();
            BlockState oldState = oldBlockState();
            world.setBlockState(pos, oldState);
            world.getBlockEntity(pos).readNbt(StringNbtReader.parse(getExtraData()));
            world.getChunkManager().markForUpdate(pos);
        }

        return true;
    }

    @Override
    public void previewRollback(Preview preview, ServerPlayerEntity player) {
        if (player.getWorld().getRegistryKey().getValue().equals(getWorld())) {
            BlockPos pos = getPos();
            BlockState oldState = oldBlockState();
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, oldState));
            preview.getPositions().add(pos);
        }
    }

    @Override
    public boolean restore(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());

        if (world != null) {
            BlockPos pos = getPos();
            BlockState newState = newBlockState();
            world.setBlockState(pos, newState);
        }

        return true;
    }

    @Override
    public void previewRestore(Preview preview, ServerPlayerEntity player) {
        if (player.getWorld().getRegistryKey().getValue().equals(getWorld())) {
            BlockPos pos = getPos();
            BlockState newState = newBlockState();
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, newState));
            preview.getPositions().add(pos);
        }
    }

    @Override
    public String getTranslationType() {
        return "block";
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        MutableText text = Text.literal("");
        text.append(
                Text.translatable(
                        Util.createTranslationKey(
                                getTranslationType(),
                                getOldObjectIdentifier()
                        )
                ).setStyle(TextColorPallet.getSecondaryVariant()).styled(style ->
                        style.withHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Text.literal(getOldObjectIdentifier().toString())
                                )
                        )
                )
        );

        if (!getOldObjectIdentifier().equals(getObjectIdentifier())) {
            text.append(Text.literal(" → "));
            text.append(
                    Text.translatable(
                            Util.createTranslationKey(
                                    getTranslationType(),
                                    getObjectIdentifier()
                            )
                    ).setStyle(TextColorPallet.getSecondaryVariant()).styled(style ->
                            style.withHoverEvent(
                                    new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            Text.literal(getObjectIdentifier().toString())
                                    )
                            )
                    )
            );
        }

        return text;
    }

    @SneakyThrows
    public BlockState oldBlockState() {
        return checkForBlockState(
                getOldObjectIdentifier(),
                getOldObjectState() != null ?
                        NbtUtils.blockStateFromProperties(StringNbtReader.parse(getOldObjectState()), getOldObjectIdentifier())
                        : null
        );
    }

    @SneakyThrows
    public BlockState newBlockState() {
        return checkForBlockState(
                getObjectIdentifier(),
                getObjectState() != null ?
                        NbtUtils.blockStateFromProperties(StringNbtReader.parse(getObjectState()), getObjectIdentifier())
                        : null
        );
    }

    private BlockState checkForBlockState(Identifier identifier, BlockState checkState) {
        Optional<Block> block = Registries.BLOCK.getOrEmpty(identifier);

        if (block.isEmpty()) {
            Logger.logWarn("Unknown block: {}", identifier);
            return Blocks.AIR.getDefaultState();
        }

        return checkState != null ? checkState : block.get().getDefaultState();
    }
}
