package com.enthusiasm.plurecore.data;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.enthusiasm.plurecore.data.annotation.DataSerializable;
import com.enthusiasm.plurecore.utils.FolderUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class DataService {
    private static final Map<String, Set<DataHolderEntry>> holderEntries = new HashMap<>();

    public static <T> Set<DataHolderEntry> register(String dataName, Class<T> dataClass) {
        Objects.requireNonNull(dataClass);

        if (holderEntries.containsKey(dataName)) {
            throw new RuntimeException(String.format("Датакласс для '%s' уже зарегистрирован", dataName));
        }

        DataSerializable definition = dataClass.getAnnotation(DataSerializable.class);
        if (definition == null) {
            throw new RuntimeException(String.format("@DataSerializable аннотация не найдена на %s!", dataClass));
        }

        Set<DataHolderEntry> dataHolderEntries = loadDataEntryHolders(dataClass,
                definition.folder() != null
                        ? definition.folder()
                        : null
        );

        holderEntries.put(dataName, dataHolderEntries);

        return dataHolderEntries;
    }

    public static <T> DataHolderEntry newDataHolder(String dataName, Class<T> dataClass) {
        Objects.requireNonNull(dataName);

        DataSerializable definition = dataClass.getAnnotation(DataSerializable.class);
        DataHolder<T> holder = new DataHolder<>(dataClass, definition, dataName);

        return new DataHolderEntry(dataName, holder, false);
    }

    private static <T> Set<DataHolderEntry> loadDataEntryHolders(
            Class<T> dataClass, String folder
    ) {
        Set<DataHolderEntry> dataHolderEntries = new HashSet<>();

        Path dataFolder = folder != null ? FolderUtils.getDataFolder(folder) : FolderUtils.getDataFolder();
        File[] dataFiles = dataFolder.toFile().listFiles((dir, name) -> name.endsWith(".dat"));

        if (dataFiles == null) {
            return dataHolderEntries;
        }

        for (File dataFile : dataFiles) {
            dataHolderEntries.add(newDataHolder(dataFile.getName().replace(".dat", ""), dataClass));
        }

        return dataHolderEntries;
    }
}