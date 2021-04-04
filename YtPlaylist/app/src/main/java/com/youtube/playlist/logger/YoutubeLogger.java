package com.youtube.playlist.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.youtube.playlist.folders.YoutubeFolder;
import com.youtube.playlist.utils.Global;

public class YoutubeLogger extends BroadcastReceiver {
   
    public static final String TAG = "YoutubeLogger";
    public static final String PACKAGE_NAME = "com.youtube.playlist";
    public static final String CLASS_NAME = PACKAGE_NAME + ".loggers.YoutubeLogger";
    public static final String EXTRA_MESSAGE = PACKAGE_NAME + ".extra.message";
    public static final Integer MAX_LOG_FILE_SIZE = 500000;
    public static final String LOG_FILE_NAME = "Logs.txt";

    public YoutubeLogger() {
    }

    public static void sendBroadcast(Context context, String message) {
        Intent intent = new Intent(context, YoutubeLogger.class);
        intent.setClassName(PACKAGE_NAME, CLASS_NAME);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
        YoutubeFolder.initFolder(new YoutubeFolder.Builder(context)
                                 .setFolderYoutube_Analytics(true)
                                 .build()); 

    }

    public static String getLogFilePath() {
        return new File(YoutubeFolder.ZFOLDER_YOUTUBE_ANALYTICS, LOG_FILE_NAME).getPath();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String logMessage = intent.getStringExtra(EXTRA_MESSAGE);
        if (logMessage.toLowerCase().contains("initialized")) {
            try {
                File logFile = new File(getLogFilePath());
                Long fileSize = Global.getDirectorySize(logFile);
                if (fileSize > MAX_LOG_FILE_SIZE) {
                    writeToLog(logMessage, false);
                    try {
                        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        writeToLog("AWeb Version " + packageInfo.versionName);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception in onReceive", e);
                    }
                } else {
                    writeToLog("---------------------------");
                    writeToLog(logMessage);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in onReceive", e);
            }
        } else {
            writeToLog(logMessage);
        }
    }

    void writeToLog(String message) {
        writeToLog(message, true);
    }

    void writeToLog(String message, boolean appendMode) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
            String time = sdf.format(new Date());

            message = time + " - " + message;

            File logFile = new File(getLogFilePath());
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, appendMode));
            bufferedWriter.newLine();
            bufferedWriter.append(message);
            bufferedWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception in writeToLog", e);
        }
    }
}

