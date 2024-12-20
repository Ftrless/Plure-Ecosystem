package com.enthusiasm.plurelogger.actions;

import java.time.Instant;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.config.ConfigWrapper;
import com.enthusiasm.plurelogger.config.PLConfig;
import com.enthusiasm.plurelogger.utils.Sources;

public interface IActionType {
    int id = -1;
    Instant timestamp = Instant.now();
    BlockPos pos = BlockPos.ORIGIN;
    Identifier world = null;
    Identifier objectIdentifier = new Identifier("air");
    Identifier oldObjectIdentifier = new Identifier("air");
    String objectState = null;
    String oldObjectState = null;
    String sourceName = Sources.UNKNOWN.getSource();
    GameProfile sourceProfile = null;
    String extraData = null;
    boolean rolledBack = false;

    int getId();
    void setId(int id);

    String getIdentifier();

    Instant getTimestamp();
    void setTimestamp(Instant timestamp);

    BlockPos getPos();
    void setPos(BlockPos pos);

    Identifier getWorld();
    void setWorld(Identifier world);

    Identifier getObjectIdentifier();
    void setObjectIdentifier(Identifier objectIdentifier);

    Identifier getOldObjectIdentifier();
    void setOldObjectIdentifier(Identifier oldObjectIdentifier);

    String getObjectState();
    void setObjectState(String objectState);

    String getOldObjectState();
    void setOldObjectState(String oldObjectState);

    String getSourceName();
    void setSourceName(String sourceName);

    GameProfile getSourceProfile();
    void setSourceProfile(GameProfile sourceProfile);

    String getExtraData();
    void setExtraData(String extraData);

    boolean isRolledBack();
    void setRolledBack(boolean rolledBack);

    boolean rollback(MinecraftServer server);
    boolean restore(MinecraftServer server);
    void previewRollback(Preview preview, ServerPlayerEntity player);
    void previewRestore(Preview preview, ServerPlayerEntity player);
    String getTranslationType();

    Text getMessage(ServerCommandSource source);

    default boolean isBlacklisted() {
        PLConfig config = ConfigWrapper.getConfig();

        return config.typeBlacklist.contains(getIdentifier()) ||
                config.objectBlacklist.contains(getObjectIdentifier().toString()) ||
                config.objectBlacklist.contains(getOldObjectIdentifier().toString()) ||
                config.sourceBlacklist.contains(getSourceName()) ||
                config.sourceBlacklist.contains("@" + (getSourceProfile() != null ? getSourceProfile().getName() : "")) ||
                config.worldBlacklist.contains(getWorld().toString());
    }
}
