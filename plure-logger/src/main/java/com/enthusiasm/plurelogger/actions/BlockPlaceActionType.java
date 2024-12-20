package com.enthusiasm.plurelogger.actions;

import lombok.SneakyThrows;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.utils.TextColorPallet;
import com.enthusiasm.plurelogger.utils.WorldUtils;

public class BlockPlaceActionType extends BlockChangeActionType {
    private final String identifier = "block-place";

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean rollback(MinecraftServer server) {
        ServerWorld world = WorldUtils.getWorld(server, getWorld());
        world.setBlockState(getPos(), oldBlockState());
        return true;
    }

    @SneakyThrows
    @Override
    public boolean restore(MinecraftServer server) {
        World world = WorldUtils.getWorld(server, getWorld());

        if (world != null) {
            world.setBlockState(getPos(), newBlockState());
            if (newBlockState().hasBlockEntity()) {
                NbtCompound nbt = StringNbtReader.parse(getExtraData());
                if (world.getBlockEntity(getPos()) != null) {
                    world.getBlockEntity(getPos()).readNbt(nbt);
                }
            }
        }
        return true;
    }

    @Override
    public Text getObjectMessage(ServerCommandSource source) {
        return Text.translatable(
                Util.createTranslationKey(getTranslationType(), getObjectIdentifier())
        ).styled(style -> style.withColor(TextColorPallet.getSecondary().getColor())
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal(getObjectIdentifier().toString())
                )));
    }
}
