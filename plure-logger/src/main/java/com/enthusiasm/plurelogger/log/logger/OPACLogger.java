//package com.plure.log.logger;
//
//import com.enthusiasm.plurecore.utils.FileUtils;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.plure.PlureLogger;
//import com.plure.helper.DateHelper;
//import com.plure.helper.IOHelper;
//import com.plure.helper.VectorHelper;
//import com.plure.log.AbstractLogger;
//import net.minecraft.server.command.ServerCommandSource;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.World;
//
//import java.io.File;
//import java.io.IOException;
//
//public class OPACLogger extends AbstractLogger {
//    private final String rootLogDir;
//    private String ftbChunksLogDir;
//
//    public OPACLogger(String rootLogDir) {
//        this.rootLogDir = rootLogDir;
//    }
//
//    @Override
//    public void init() {
//        File chunksDir = new File(this.rootLogDir, "opac");
//        FileUtils.createFolderAsync(chunksDir);
//
//        this.ftbChunksLogDir = chunksDir.getAbsolutePath();
//    }
//
//    @Override
//    public void subscribeToEvent() {
////        ClaimedChunkEvent.AFTER_CLAIM.register(((serverCommandSource, claimedChunk) -> {
////            try {
////                this.handleEvent(serverCommandSource, claimedChunk, true);
////            } catch (CommandSyntaxException ignored) {}
////        }));
////        ClaimedChunkEvent.AFTER_UNCLAIM.register(((serverCommandSource, claimedChunk) -> {
////            try {
////                this.handleEvent(serverCommandSource, claimedChunk, false);
////            } catch (CommandSyntaxException ignored) {}
////        }));
//
//    }
//
//    private void handleEvent(ServerCommandSource serverCommandSource, ClaimedChunk claimedChunk, boolean claim) throws CommandSyntaxException {
//        ServerPlayerEntity sourcePlayer = serverCommandSource.getPlayerOrThrow();
//        RegistryKey<World> world = claimedChunk.getPos().dimension;
//        ChunkPos chunkPos = claimedChunk.getPos().getChunkPos();
//        String type = claim ? "Приватизация" : "Отмена приватизации";
//
//        String formattedDate = DateHelper.getDate();
//
//        File logFile = IOHelper.initLogFile(this.ftbChunksLogDir, this.ftbChunksLogDir, DateHelper.SHORT_PATTERN_DATE, false);
//
//        try {
//            String content = String.format(
//                    "[%s, %s, %s] %s чанка игроком %s. Команда: %s\n",
//                    formattedDate,
//                    world.getValue(),
//                    VectorHelper.toVec(chunkPos.getStartPos()),
//                    type,
//                    sourcePlayer.getEntityName(),
//                    claimedChunk.getTeamData().getTeam().getDisplayName()
//            );
//
//            IOHelper.writeFileAsync(logFile, content);
//        } catch (IOException e) {
//            PlureLogger.LOGGER.error("Ошибка записи лога чанков: {}", e.getMessage());
//        }
//    }
//}
