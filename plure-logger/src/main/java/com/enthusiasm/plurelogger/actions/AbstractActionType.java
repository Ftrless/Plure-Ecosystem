package com.enthusiasm.plurelogger.actions;

import java.time.Instant;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.utils.MessageUtils;
import com.enthusiasm.plurelogger.utils.Sources;
import com.enthusiasm.plurelogger.utils.TextColorPallet;

public abstract class AbstractActionType implements IActionType {
    private int id = -1;
    private Instant timestamp = Instant.now();
    private BlockPos pos = BlockPos.ORIGIN;
    private Identifier world = null;
    private Identifier objectIdentifier = new Identifier("air");
    private Identifier oldObjectIdentifier = new Identifier("air");
    private String objectState = null;
    private String oldObjectState = null;
    private String sourceName = Sources.UNKNOWN.getSource();
    private GameProfile sourceProfile = null;
    private String extraData = null;
    private boolean rolledBack = false;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Identifier getWorld() {
        return world;
    }

    @Override
    public void setWorld(Identifier world) {
        this.world = world;
    }

    @Override
    public Identifier getObjectIdentifier() {
        return objectIdentifier;
    }

    @Override
    public void setObjectIdentifier(Identifier objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    @Override
    public Identifier getOldObjectIdentifier() {
        return oldObjectIdentifier;
    }

    @Override
    public void setOldObjectIdentifier(Identifier oldObjectIdentifier) {
        this.oldObjectIdentifier = oldObjectIdentifier;
    }

    @Override
    public String getObjectState() {
        return objectState;
    }

    @Override
    public void setObjectState(String objectState) {
        this.objectState = objectState;
    }

    @Override
    public String getOldObjectState() {
        return oldObjectState;
    }

    @Override
    public void setOldObjectState(String oldObjectState) {
        this.oldObjectState = oldObjectState;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public GameProfile getSourceProfile() {
        return sourceProfile;
    }

    @Override
    public void setSourceProfile(GameProfile sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    @Override
    public String getExtraData() {
        return extraData;
    }

    @Override
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public boolean isRolledBack() {
        return rolledBack;
    }

    @Override
    public void setRolledBack(boolean rolledBack) {
        this.rolledBack = rolledBack;
    }

    @Override
    public boolean rollback(MinecraftServer server) {
        return false;
    }

    @Override
    public void previewRollback(Preview preview, ServerPlayerEntity player) {
        // No-op
    }

    @Override
    public void previewRestore(Preview preview, ServerPlayerEntity player) {
        // No-op
    }

    @Override
    public boolean restore(MinecraftServer server) {
        return false;
    }

    @Override
    public Text getMessage(ServerCommandSource source) {
        MutableText message = Text.translatable(
                "text.action_message",
                getTimeMessage(),
                getSourceMessage(),
                getActionMessage(),
                getObjectMessage(source),
                getLocationMessage()
        ).setStyle(TextColorPallet.getLight());

        if (rolledBack) {
            message.formatted(Formatting.STRIKETHROUGH);
        }

        return message;
    }

    public Text getTimeMessage() {
        return MessageUtils.instantToText(timestamp);
    }

    public Text getSourceMessage() {
        if (sourceProfile == null) {
            return Text.literal("@" + sourceName).setStyle(TextColorPallet.getSecondary());
        }

        if (sourceName.equals(Sources.PLAYER.getSource())) {
            return Text.literal(sourceProfile.getName()).setStyle(TextColorPallet.getSecondary());
        }

        return Text.literal("@" + sourceName + " (" + sourceProfile.getName() + ")").setStyle(TextColorPallet.getSecondary());
    }

    public Text getActionMessage() {
        return Text.translatable("text.action." + getIdentifier())
                .styled(style -> style.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(getIdentifier()))
                ));
    }

    public Text getObjectMessage(ServerCommandSource source) {
        return Text.translatable(
                Util.createTranslationKey(
                        getTranslationType(),
                        objectIdentifier
                )
        ).setStyle(TextColorPallet.getSecondaryVariant()).styled(style -> style.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(objectIdentifier.toString()))
        ));
    }

    public Text getLocationMessage() {
        return Text.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ())
                .setStyle(TextColorPallet.getSecondary())
                .styled(style -> style.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(world != null ? world + "\n" : "")
                                .append(Text.translatable("text.action_message.location.hover"))
                        )
                ).withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/l tp " + (world != null ? world : World.OVERWORLD.getValue()) + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                        )
                ));
    }
}
