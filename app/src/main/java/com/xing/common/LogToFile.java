package com.xing.common;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogToFile {

    private static final String TAG = "LogToFile";
    private static final String LOG_FILE_NAME = "/sdcard/hptc_logfile.txt";

    private static void writeLogToFile(String tag, String msg) {
        File logFile = new File(LOG_FILE_NAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(tag, "Could not create log file", e);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            String logMsg = tag + ": " + msg + "\n";
            fos.write(logMsg.getBytes());
        } catch (IOException e) {
            Log.e(tag, "Could not write to log file", e);
        }
    }

    public static void d(String tag, String msg) {
        writeLogToFile(tag, msg);
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        writeLogToFile(tag, msg);
        Log.e(tag, msg);
    }

    // Add more methods for other log levels as needed
}