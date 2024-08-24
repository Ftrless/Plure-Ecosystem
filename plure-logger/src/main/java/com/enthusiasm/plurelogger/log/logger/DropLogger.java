package com.enthusiasm.plurelogger.log.logger;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import com.enthusiasm.plurecore.utils.FileUtils;
import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurelogger.event.PlayerDropCallback;
import com.enthusiasm.plurelogger.helper.DateHelper;
import com.enthusiasm.plurelogger.helper.IDHelper;
import com.enthusiasm.plurelogger.helper.IOHelper;
import com.enthusiasm.plurelogger.helper.VectorHelper;
import com.enthusiasm.plurelogger.log.AbstractLogger;

public class DropLogger extends AbstractLogger {
    private final String rootLogDir;
    private String dropLogDir;

    public DropLogger(String rootLogDir) {
        this.rootLogDir = rootLogDir;
    }

    @Override
    public void init() {
        File dropsDir = new File(this.rootLogDir, "drop");
        FolderUtils.createFolderAsync(dropsDir.getAbsolutePath());

        this.dropLogDir = dropsDir.getAbsolutePath();
    }

    @Override
    public void subscribeToEvent() {
        PlayerDropCallback.EVENT.register(this::handleEvent);
    }

    private void handleEvent(ServerPlayerEntity targetPlayer, boolean drop, ItemStack itemStack) {
        ServerWorld playerWorld = targetPlayer.getServerWorld();
        Vec3d playerPos = VectorHelper.getRoundedPos(targetPlayer.getPos());
        String type = drop ? "Выборсил" : "Поднял";

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile(targetPlayer.getEntityName(), this.dropLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        String content = String.format(
                "[%s, %s, %s] (%s) %s (x%s)\n",
                formattedDate,
                playerWorld.getRegistryKey().getValue(),
                playerPos,
                type,
                IDHelper.getIdentifier(itemStack.getTranslationKey()),
                itemStack.getCount()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
}
