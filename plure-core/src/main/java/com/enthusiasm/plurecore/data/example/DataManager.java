package com.enthusiasm.plurecore.data.example;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.enthusiasm.plurecore.data.DataHolder;
import com.enthusiasm.plurecore.data.DataHolderEntry;
import com.enthusiasm.plurecore.data.DataService;

import java.util.Set;
import java.util.UUID;

public class DataManager {
    public static void init() {
        Set<DataHolderEntry> dataHolderEntries = DataService.register("warp", WarpData.class);

        if (dataHolderEntries.isEmpty()) {
            PlureCoreEntrypoint.LOGGER.info("Датахолдер пуст, добавляем новый энтри и сохраняем");
            DataService.newDataHolder(UUID.randomUUID().toString(), WarpData.class);
            return;
        }

        for (DataHolderEntry dataHolderEntry : dataHolderEntries) {
            DataHolder<WarpData> dataHolder = (DataHolder<WarpData>) dataHolderEntry.dataHolder();
            WarpData warpData = dataHolder.getDataClassDefinition();
        }
    }
}
