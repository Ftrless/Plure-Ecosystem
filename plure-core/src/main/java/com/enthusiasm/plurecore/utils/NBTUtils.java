package com.enthusiasm.plurecore.utils;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.List;
import java.util.UUID;

public class NBTUtils {
    public static void writeTagFromPrimaryType(NbtCompound tag, String key, Object value) {
        switch (value) {
            case Integer i -> tag.putInt(key, i);
            case String s -> tag.putString(key, s);
            case Boolean b -> tag.putBoolean(key, b);
            case Byte b -> tag.putByte(key, b);
            case Short i -> tag.putShort(key, i);
            case Long l -> tag.putLong(key, l);
            case Float v -> tag.putFloat(key, v);
            case Double v -> tag.putDouble(key, v);
            case byte[] bytes -> tag.putByteArray(key, bytes);
            case int[] intArray -> tag.putIntArray(key, intArray);
            case String[] list -> {
                NbtList nbtList = new NbtList();
                for (String s : list) {
                    nbtList.add(NbtString.of(s));
                }
                tag.put(key, nbtList);
            }
            case UUID uuid -> tag.putUuid(key, uuid);
            case NbtCompound nbtCompound -> tag.put(key, nbtCompound);
            case null, default -> throw new IllegalArgumentException("Unsupported type for value: " + value);
        }
    }

    public static Object getPrimaryTypeFromTag(NbtCompound tag, String key, Class<?> type) {
        return switch (type.getSimpleName()) {
            case "Integer", "int" -> tag.getInt(key);
            case "String" -> tag.getString(key);
            case "Boolean", "boolean" -> tag.getBoolean(key);
            case "Byte", "byte" -> tag.getByte(key);
            case "Short", "short" -> tag.getShort(key);
            case "Long", "long" -> tag.getLong(key);
            case "Float", "float" -> tag.getFloat(key);
            case "Double", "double" -> tag.getDouble(key);
            case "byte[]" -> tag.getByteArray(key);
            case "int[]" -> tag.getIntArray(key);
            case "String[]" -> {
                NbtList nbtList = tag.getList(key, NbtElement.STRING_TYPE);
                String[] array = new String[nbtList.size()];
                for (int i = 0; i < nbtList.size(); i++) {
                    array[i] = nbtList.getString(i);
                }
                yield array;
            }
            case "UUID" -> tag.getUuid(key);
            case "NbtCompound" -> tag.getCompound(key);
            default -> throw new IllegalArgumentException("Unsupported type for class: " + type);
        };
    }
}
