package com.enthusiasm.plurechat.data;

import com.enthusiasm.plurechat.PlureChatEntrypoint;
import com.enthusiasm.plurecore.data.DataHolder;
import com.enthusiasm.plurecore.data.DataHolderEntry;
import com.enthusiasm.plurecore.data.DataService;

import java.util.*;
import java.util.stream.Stream;

public class DataManager {
    private static final Map<UUID, List<UUID>> users = new HashMap<>();

    public static void init () {
        Set<DataHolderEntry> dataHolderEntries = DataService.register("chat", ChatData.class);

        if (dataHolderEntries.isEmpty()) {
            return;
        }

        for (DataHolderEntry dataHolderEntry : dataHolderEntries) {
            DataHolder<ChatData> dataHolder = (DataHolder<ChatData>) dataHolderEntry.dataHolder();
            ChatData chatData = dataHolder.getDataClassDefinition();

            List<UUID> ignorableUUIDs = Arrays.stream(chatData.ignorableUUIDs)
                    .map(UUID::fromString)
                    .toList();
            users.put(UUID.fromString(dataHolderEntry.userUUID()), new ArrayList<>(ignorableUUIDs));
        }
    }

    public static void addOrUpdateIgnorableUser(UUID userUUID, UUID ignorableUUID) {
        List<UUID> ignorableUUIDs = users.computeIfAbsent(userUUID, k -> new ArrayList<>());

        ignorableUUIDs.add(ignorableUUID);
        users.put(userUUID, ignorableUUIDs);
        saveChanges(userUUID);
    }

    public static boolean checkContainsIgnorableUser(UUID userUUID, UUID ignorableUUID) {
        return users.computeIfAbsent(userUUID, k -> new ArrayList<>()).contains(ignorableUUID);
    }

    public static void removeIgnorableUser(UUID userUUID, UUID ignorableUUID) {
        List<UUID> ignorableUUIDs = users.get(userUUID);

        if (ignorableUUIDs != null && ignorableUUIDs.remove(ignorableUUID)) {
            users.put(userUUID, ignorableUUIDs);
            saveChanges(userUUID);
        }
    }

    private static void saveChanges(UUID userUUID) {
        List<String> ignorableUUIDs = users.get(userUUID).stream().map(UUID::toString).toList();;

        Set<DataHolderEntry> dataHolderEntries = DataService.getDataHolderEntries("chat");
        Stream<DataHolderEntry> filteredEntries = dataHolderEntries.stream()
                .filter(dataHolderEntry -> dataHolderEntry.userUUID().equals(userUUID.toString()));

        if (filteredEntries.findAny().isEmpty()) {
            dataHolderEntries.add(DataService.newDataHolder(userUUID.toString(), ChatData.class));
        }

        for (DataHolderEntry dataHolderEntry : dataHolderEntries) {
            if (!dataHolderEntry.userUUID().equals(userUUID.toString())) {
                continue;
            }

            DataHolder<ChatData> dataHolder = (DataHolder<ChatData>) dataHolderEntry.dataHolder();
            ChatData chatData = dataHolder.getDataClassDefinition();

            chatData.ignorableUUIDs = ignorableUUIDs.toArray(new String[0]);
            dataHolder.saveData();

            break;
        }
    }
}
