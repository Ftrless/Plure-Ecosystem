package com.enthusiasm.plurechat;

import com.enthusiasm.plurechat.data.DataManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlureChatEntrypoint implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlureChat");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing PlureChat");

        CommandRegistrationCallback.EVENT.register(CommandRegistry::register);

        ChatService.init();
        DataManager.init();
    }
}
