package com.enthusiasm.plurelogger.registry;

import com.enthusiasm.plurelogger.command.CommandRegistry;
import com.enthusiasm.plurelogger.listener.*;

public class ModRegistry {
    public static void registerEvents() {
        CommandRegistry.init();

        BlockListeners.init();
        EntityListeners.init();
        PlayerListeners.init();
        ServerListeners.init();
        WorldListeners.init();
    }
}
