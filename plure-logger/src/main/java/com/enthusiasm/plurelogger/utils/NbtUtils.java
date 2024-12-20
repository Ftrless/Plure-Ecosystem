package com.enthusiasm.plurelogger.utils;

import lombok.SneakyThrows;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class NbtUtils {
    public static final String PROPERTIES = "Properties"; // BlockState
    public static final String COUNT = "Count"; // ItemStack
    public static final String TAG = "tag"; // ItemStack
    public static final String UUID = "UUID"; // Entity

    /**
     * Converts a BlockState to NbtCompound properties.
     *
     * @param state The BlockState to convert.
     * @return The properties NbtCompound or null if the state is the default state.
     */
    public static NbtCompound blockStateToProperties(BlockState state) {
        NbtCompound stateTag = NbtHelper.fromBlockState(state);

        if (state.getBlock().getDefaultState().equals(state)) {
            return stateTag;
        }

        if (stateTag.contains(PROPERTIES, NbtElement.COMPOUND_TYPE)) {
            return stateTag.getCompound(PROPERTIES);
        } else {
            return stateTag;
        }
    }

    /**
     * Creates a BlockState from given properties and block name.
     *
     * @param tag  The NbtCompound containing the properties.
     * @param name The Identifier of the block.
     * @return The constructed BlockState.
     */
    public static BlockState blockStateFromProperties(NbtCompound tag, Identifier name) {
        NbtCompound stateTag = new NbtCompound();
        stateTag.putString("Name", name.toString());
        stateTag.put(PROPERTIES, tag);

        return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), stateTag);
    }

    public static NbtCompound itemToProperties(ItemStack item) {
        return item.writeNbt(new NbtCompound());
    }

    /**
     * Creates an ItemStack from given properties, name, and registry lookup.
     *
     * @param tag        The Nbt data in string format.
     * @param name       The Identifier of the item.
     * @param registries The registry wrapper lookup.
     * @return The constructed ItemStack.
     */
    @SneakyThrows
    public static ItemStack itemFromProperties(String tag, Identifier name, RegistryWrapper.WrapperLookup registries) {
        return ItemStack.fromNbt(StringNbtReader.parse(tag != null ? tag : "{}"));
    }
}