package com.enthusiasm.plurecore.utils;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FileUtils {
    /**
     * Асинхронно записывает контент в файл.
     *
     * @param filePath Путь к файлу, в который будет записан контент.
     * @param content  Контент для записи в файл.
     * @return CompletableFuture, который завершится, когда операция записи будет выполнена.
     */
    public static void writeFileAsync(Path filePath, String content) {
        writeFileAsync(filePath, content, true);
    }

    /**
     * Асинхронно записывает контент в файл с возможностью добавления.
     *
     * @param filePath Путь к файлу, в который будет записан контент.
     * @param content  Контент для записи в файл.
     * @param append   Если true, контент будет добавлен в конец файла, иначе файл будет перезаписан.
     * @return CompletableFuture, который завершится, когда операция записи будет выполнена.
     */
    public static void writeFileAsync(Path filePath, String content, boolean append) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
            long position = append ? fileChannel.size() : 0;

            fileChannel.write(buffer, position).get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            PlureCoreEntrypoint.LOGGER.error("Ошибка записи файла: {}", e.getMessage());
        }
    }

    /**
     * Асинхронно читает контент из файла.
     *
     * @param filePath Путь к файлу, из которого будет прочитан контент.
     * @return CompletableFuture, содержащий контент файла, когда операция чтения будет выполнена.
     */
    public static String readFileAsync(Path filePath) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) Files.size(filePath));
            fileChannel.read(buffer, 0).get();
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            fileChannel.close();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException | ExecutionException | InterruptedException e) {
            PlureCoreEntrypoint.LOGGER.error("Ошибка чтения файла: {}", e.getMessage());
            return null;
        }
    }
}
