package com.enthusiasm.plurelogger.registry;

import java.util.Arrays;

import com.enthusiasm.plurelogger.storage.database.maria.DatabaseService;
import com.enthusiasm.plurelogger.utils.Sources;

public class SourcesRegistry {
    public static void registerSources() {
        Arrays.stream(Sources.values())
                .map(Sources::getSource)
                .forEach(DatabaseService::registerSource);
    }
}
