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
import java.util.function.Supplier;

public class FileUtils {
    public static CompletableFuture<Void> writeFileAsync(Path filePath, String content) {
        return writeFileAsync(filePath, content, true);
    }

    public static CompletableFuture<Void> writeFileAsync(Path filePath, String content, boolean append) {
        Supplier<Void> task = () -> {
            try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath,
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                ByteBuffer buffer = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
                fileChannel.write(buffer, append ? fileChannel.size() : 0).get();
            } catch (IOException | InterruptedException | ExecutionException e) {
                PlureCoreEntrypoint.LOGGER.error("Ошибка записи файла: {}", e.getMessage());
            }

            return null;
        };

        return ThreadUtils.runAsync(task);
    }

    public static CompletableFuture<String> readFileAsync(Path filePath) {
        Supplier<String> task = () -> {
            try {
                AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ);
                ByteBuffer buffer = ByteBuffer.allocate((int) Files.size(filePath));
                fileChannel.read(buffer, 0).get();
                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                fileChannel.close();
                return new String(bytes, StandardCharsets.UTF_8);
            } catch (IOException | InterruptedException | ExecutionException e) {
                PlureCoreEntrypoint.LOGGER.error("Ошибка чтения файла: {}", e.getMessage());
                return null;
            }
        };

        return ThreadUtils.runAsync(task);
    }
}
