package com.enthusiasm.plureutils.command.util;

import com.enthusiasm.plurecore.config.ConfigHolder;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plureutils.command.CommandHelper;
import com.enthusiasm.plureutils.config.ConfigManager;
import com.enthusiasm.plureutils.config.PUConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

public class ConfigReload implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    public void exec(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ConfigHolder<PUConfig> configHolder = ConfigManager.getConfigHolder();

        MutableText failedToReload = TextUtils.translation("cmd.config.reload.error", FormatUtils.Colors.ERROR);
        boolean configReloaded = configHolder.loadConfig();

        if (!configReloaded) {
            throw CommandHelper.createException(failedToReload);
        }

        ConfigManager.setConfig(configHolder.getConfig());

        PlayerUtils.sendFeedback(context, "cmd.config.reload.feedback");
    }
}
