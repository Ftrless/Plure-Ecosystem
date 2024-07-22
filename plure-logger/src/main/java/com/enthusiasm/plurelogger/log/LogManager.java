package com.enthusiasm.plurelogger.log;

import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurelogger.log.logger.ChatLogger;
import com.enthusiasm.plurelogger.log.logger.DeathLogger;
import com.enthusiasm.plurelogger.log.logger.DropLogger;
import com.enthusiasm.plurelogger.log.logger.FtbTeamsLogger;

import java.io.File;

public class LogManager {
    public static String rootLogDir;
    
    public static void init() {
        String minecraftRoot = System.getProperty("user.dir");

        File logsDir = new File(minecraftRoot, "logs");
        File plureLogsDir = new File(logsDir, "plurelogs");

        FolderUtils.createFolderAsync(String.valueOf(plureLogsDir));

        rootLogDir = plureLogsDir.getAbsolutePath();
    }

    public static void initLoggers() {
        initLogger(new DeathLogger(rootLogDir));
        initLogger(new DropLogger(rootLogDir));
        //initLogger(new OPACLogger(rootLogDir));
        initLogger(new FtbTeamsLogger(rootLogDir));
        initLogger(new ChatLogger(rootLogDir));
    }

    private static void initLogger(AbstractLogger logger) {
        logger.init();
        logger.subscribeToEvent();
    }
}
