package com.enthusiasm.plurelogger.log.logger;

import java.io.File;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import com.enthusiasm.plurechat.api.MessageEventsAPI;
import com.enthusiasm.plurecore.utils.FileUtils;
import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurelogger.event.PlayerConnectCallback;
import com.enthusiasm.plurelogger.helper.DateHelper;
import com.enthusiasm.plurelogger.helper.IOHelper;
import com.enthusiasm.plurelogger.log.AbstractLogger;

public class ChatLogger extends AbstractLogger {
    private final String rootLogDir;
    private String chatLogDir;

    public ChatLogger(String rootLogDir) {
        this.rootLogDir = rootLogDir;
    }

    @Override
    public void init() {
        File chatDir = new File(this.rootLogDir, "chat");
        FolderUtils.createFolderAsync(chatDir.getAbsolutePath());

        this.chatLogDir = chatDir.getAbsolutePath();
    }

    @Override
    public void subscribeToEvent() {
        MessageEventsAPI.CHAT_MESSAGE.register(this::handleMessageEvent);
        MessageEventsAPI.PRIVATE_CHAT_MESSAGE.register(this::handleMessageEvent);
        MessageEventsAPI.COMMAND_EXECUTE.register(this::handleCommandEvent);
        PlayerConnectCallback.EVENT.register(this::handleConnectEvent);
    }

    private void handleMessageEvent(MutableText message) {
        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile(this.chatLogDir, this.chatLogDir, DateHelper.SHORT_PATTERN_DATE, false);

        String content = String.format(
                "[%s] %s\n",
                formattedDate,
                message.getString()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handleConnectEvent(ServerPlayerEntity player, boolean connect) {
        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile(this.chatLogDir, this.chatLogDir, DateHelper.SHORT_PATTERN_DATE, false);

        String content = String.format(
                "[%s] Игрок %s %s.\n",
                formattedDate,
                player.getEntityName(),
                connect ? "зашел на сервер" : "вышел с сервера"
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handleCommandEvent(String command, ServerPlayerEntity executor) {
        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile(this.chatLogDir, this.chatLogDir, DateHelper.SHORT_PATTERN_DATE, false);

        String content = String.format(
                "[%s] Игрок %s использовал команду %s.\n",
                formattedDate,
                executor.getEntityName(),
                command
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
}
