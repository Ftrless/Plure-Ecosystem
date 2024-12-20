package com.enthusiasm.plurelogger;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.actionutils.Preview;

public class CacheService {
    @Getter private final static ConcurrentHashMap<String, ActionSearchParams> searchCache = new ConcurrentHashMap<>();
    @Getter private final static ConcurrentHashMap<UUID, Preview> previewCache = new ConcurrentHashMap<>();
}
