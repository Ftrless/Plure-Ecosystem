package com.enthusiasm.plurelogger.registry;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import com.enthusiasm.plurelogger.actions.*;
import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;

public final class ActionRegistry {
    private static final int MAX_LENGTH = 16;
    private static final Object2ObjectOpenHashMap<String, Supplier<IActionType>> actionTypes = new Object2ObjectOpenHashMap<>();

    public static void registerActionType(Supplier<IActionType> supplier) {
        IActionType actionType = supplier.get();
        String id = actionType.getIdentifier();

        if (id.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Action type ID length exceeds maximum length");
        }

        actionTypes.putIfAbsent(id, supplier);
        DatabaseService.registerActionType(id);
    }

    public static void registerDefaultTypes() {
        registerActionType(BlockBreakActionType::new);
        registerActionType(BlockPlaceActionType::new);
        registerActionType(BlockChangeActionType::new);
        registerActionType(ItemInsertActionType::new);
        registerActionType(ItemRemoveActionType::new);
        registerActionType(ItemPickUpActionType::new);
        registerActionType(ItemDropActionType::new);
        registerActionType(EntityKillActionType::new);
        registerActionType(EntityChangeActionType::new);
    }

    public static IActionType getType(String id) {
        return actionTypes.get(id).get();
    }

    public static ObjectSet<String> getTypes() {
        return actionTypes.keySet();
    }
}
