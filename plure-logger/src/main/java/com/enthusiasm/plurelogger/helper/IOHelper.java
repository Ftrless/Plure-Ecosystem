package com.enthusiasm.plurelogger.helper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.enthusiasm.plurecore.utils.FolderUtils;

public class IOHelper {
    public static File initLogFile(String target, String logDir, String pattern, boolean createDir) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String formattedDate = dateFormat.format(new Date());

        File playerDir = createDir ? new File(logDir, target) : new File(logDir);

        if (createDir) {
            FolderUtils.createFolderAsync(playerDir.getAbsolutePath());
        }

        return new File(playerDir, formattedDate + ".txt");
    }
}
