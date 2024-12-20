package com.enthusiasm.plurelogger.storage.database.maria;

import java.util.UUID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.util.Identifier;

public class DatabaseCacheService {
    public final Cache<String, Integer> actionIdentifierKeys = CacheBuilder.newBuilder().build();
    public final Cache<Identifier, Integer> worldIdentifierKeys = CacheBuilder.newBuilder().build();
    public final Cache<Identifier, Integer> objectIdentifierKeys = CacheBuilder.newBuilder().build();
    public final Cache<String, Integer> sourceKeys = CacheBuilder.newBuilder().build();
    public final Cache<UUID, Integer> playerKeys = CacheBuilder.newBuilder().build();

    private static final DatabaseCacheService INSTANCE = new DatabaseCacheService();
    public static DatabaseCacheService getInstance() {
        return INSTANCE;
    }
}
