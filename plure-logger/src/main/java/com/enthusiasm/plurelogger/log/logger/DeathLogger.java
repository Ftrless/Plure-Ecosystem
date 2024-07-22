package com.enthusiasm.plurelogger.log.logger;

import com.enthusiasm.plurecore.utils.FileUtils;
import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurelogger.PlureLoggerEntrypoint;
import com.enthusiasm.plurelogger.event.PlayerDeathCallback;
import com.enthusiasm.plurelogger.helper.DateHelper;
import com.enthusiasm.plurelogger.helper.IDHelper;
import com.enthusiasm.plurelogger.helper.IOHelper;
import com.enthusiasm.plurelogger.log.AbstractLogger;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.IOException;

public class DeathLogger extends AbstractLogger {
    private final String rootLogDir;
    private String deathLogDir;

    public DeathLogger(String rootLogDir) {
        this.rootLogDir = rootLogDir;
    }

    @Override
    public void init() {
        File deathsDir = new File(this.rootLogDir, "deaths");
        FolderUtils.createFolderAsync(deathsDir.getAbsolutePath());

        this.deathLogDir = deathsDir.getAbsolutePath();
    }

    @Override
    public void subscribeToEvent() {
        PlayerDeathCallback.EVENT.register(this::handleEvent);
    }

    private void handleEvent(ServerPlayerEntity targetPlayer, DamageSource damageSource) {
        PlayerInventory playerInventory = targetPlayer.getInventory();
        ServerWorld playerWorld = targetPlayer.getServerWorld();
        Vec3d playerPos = targetPlayer.getPos();

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile(targetPlayer.getEntityName(), this.deathLogDir, DateHelper.LONG_PATTERN_DATE, true);

        try {
            StringBuilder content = new StringBuilder();
            content.append("Дата => ").append(formattedDate).append("\n");
            content.append("Локация => ")
                    .append(playerWorld.getRegistryKey().getValue())
                    .append(" ")
                    .append(playerPos.toString())
                    .append("\n");
            content.append("Причина => ").append(damageSource.getDeathMessage(targetPlayer).getString()).append("\n");
            content.append("Инвентарь:\n");

            appendInventorySection(content, "\t* Основной инвентарь", playerInventory.main);
            appendInventorySection(content, "\t* Броня", playerInventory.armor);
            appendInventorySection(content, "\t* 2 рука", playerInventory.offHand);

            FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content.toString());
        } catch (IOException e) {
            PlureLoggerEntrypoint.LOGGER.error("Ошибка записи лога смерти: {}", e.getMessage());
        }
    }

    private void appendInventorySection(StringBuilder content, String sectionName, DefaultedList<ItemStack> items) throws IOException {
        content.append(sectionName).append(":\n");
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                content.append("\t\t- ")
                        .append(itemStack.getName().getString())
                        .append(" (").append(IDHelper.getIdentifier(itemStack.getTranslationKey()))
                        .append(") (x")
                        .append(itemStack.getCount()).append(")\n");
            }
        }
    }
}
