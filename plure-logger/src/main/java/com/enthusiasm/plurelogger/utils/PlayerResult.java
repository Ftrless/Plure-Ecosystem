package com.enthusiasm.plurelogger.utils;

import java.time.Instant;
import java.util.UUID;

import net.minecraft.text.Text;

import com.enthusiasm.plurelogger.storage.database.maria.entity.PlayerEntity;

public record PlayerResult(UUID uuid, String name, Instant firstJoin, Instant lastJoin) {
    public Text toText() {
        Text nameText = Text.literal(name).setStyle(TextColorPallet.getLight());
        Text firstJoinText = MessageUtils.instantToText(firstJoin).setStyle(TextColorPallet.getPrimaryVariant());
        Text lastJoinText = MessageUtils.instantToText(lastJoin).setStyle(TextColorPallet.getPrimaryVariant());

        return Text.translatable(
                "text.player.result",
                nameText,
                firstJoinText,
                lastJoinText
        ).setStyle(TextColorPallet.getSecondary());
    }

    public static PlayerResult fromEntity(PlayerEntity playerEntity) {
        return new PlayerResult(
                UUID.fromString(playerEntity.getPlayerId()),
                playerEntity.getPlayerName(),
                playerEntity.getFirstJoin(),
                playerEntity.getLastJoin()
        );
    }
}
