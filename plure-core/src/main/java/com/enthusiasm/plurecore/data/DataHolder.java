package com.enthusiasm.plurecore.data;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.enthusiasm.plurecore.data.annotation.DataAttribute;
import com.enthusiasm.plurecore.data.annotation.DataSerializable;
import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurecore.utils.NBTUtils;
import com.enthusiasm.plurecore.utils.ReflectUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataHolder<T> {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureCore-Data");

    private final DataSerializable annotation;
    private final Class<T> dataClass;
    private final String playerUUID;
    private T dataClassDefinition;

    public DataHolder(Class<T> dataClass, DataSerializable annotation, String playerUUID) {
        this.annotation = annotation;
        this.dataClass = dataClass;
        this.playerUUID = playerUUID;

        if (loadData()) {
            saveData();
        }
    }

    public void saveData() {
        File dataFile = getDataFile();
        ensureFileCreate(dataFile);

        NbtCompound nbtData = new NbtCompound();
        NbtCompound nbtCompound = new NbtCompound();

        for (Field field : getDataClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(getDataClassDefinition());
                String propertyName = field.getName();

                if (field.isAnnotationPresent(DataAttribute.class)) {
                    NBTUtils.writeTagFromPrimaryType(nbtCompound, propertyName, value);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Произошла ошибка при сохранении даты.", e);
            }
        }

        nbtData.put("data", nbtCompound);
        NbtHelper.putDataVersion(nbtData);

        try {
            NbtIo.writeCompressed(nbtData, dataFile);
        } catch (IOException e) {
            PlureCoreEntrypoint.LOGGER.error("Ошибка сохранения для '{}': {}", getPlayerUUID(), e);
        }
    }

    public boolean loadData() {
        File dataFile = getDataFile();
        ensureFileCreate(dataFile);

        dataClassDefinition = ReflectUtils.constructUnsafely(getDataClass());

        for (Field field : getDataClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                NbtCompound nbtData = NbtIo.readCompressed(dataFile).getCompound("data");
                String propertyName = field.getName();

                if (field.isAnnotationPresent(DataAttribute.class)) {
                    Object value = NBTUtils.getPrimaryTypeFromTag(nbtData, propertyName, field.getType());

                    field.set(dataClassDefinition, value);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Произошла ошибка при загрузке даты.", e);
                return false;
            } catch (IOException e) {
                return e instanceof EOFException;
            }
        }

        return true;
    }

    public DataSerializable getAnnotation() {
        return annotation;
    }

    @NotNull
    public Class<T> getDataClass() {
        return dataClass;
    }

    public T getDataClassDefinition() {
        return dataClassDefinition;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    private void ensureFileCreate(File dataFile) {
        try {
            Files.createDirectories(dataFile.getParentFile().toPath());
        } catch (IOException e) {
            LOGGER.error("Произошла ошибка при создании директории.", e);
        }
    }

    private File getDataFile() {
        DataSerializable definition = getAnnotation();
        String dataName = getPlayerUUID();

        Path baseFolder = FolderUtils.getDataFolder();
        Path dataPath = definition.folder() != null ? baseFolder.resolve(definition.folder()) : baseFolder;

        return new File(dataPath.toFile(), dataName + ".dat");
    }
}
