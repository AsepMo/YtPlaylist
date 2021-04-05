package com.youtube.playlist.utils;

import android.util.Log;

import java.io.File;

/**
 * @author Orhan Obut
 */
public final class YoutubeChecker {

    private static final String TAG = YoutubeChecker.class.getSimpleName();

    /**
     * Contains all possible places to check binaries
     */
    private static final String[] pathList;
	private static final String[] YoutubeFile;
	
    /**
     * The binary which grants the root privileges
     */
    private static final String KEY_SU = "su";
	private static final String KEY_YOUTUBE = "youtube.json";
	
    static {
        pathList = new String[]{
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/"
        };
    }

	static {
        YoutubeFile = new String[]{
			"/ZFOLDER/",
			"/ZFOLDER/4.Video/2.Youtube/Analytics",
			"/ZFOLDER/4.Video/2.Youtube/Download",
        };
    }
	
    public static boolean isDeviceRooted() {
        return doesFileExists(KEY_SU);
    }

	public static boolean isYoutubeFileExist() {
        return doesYtFileExists(KEY_YOUTUBE);
    }
	
	public static boolean isYoutubeFileExist(String KEY_FILE) {
        return doesYtFileExists(KEY_FILE);
    }
    /**
     * Checks the all path until it finds it and return immediately.
     *
     * @param value must be only the binary name
     * @return if the value is found in any provided path
     */
    private static boolean doesFileExists(String value) {
        boolean result = false;
        for (String path : pathList) {
            File file = new File(path + "/" + value);
            result = file.exists();
            if (result) {
                Log.d(TAG, path + " contains su binary");
                break;
            }
        }
        return result;
    }
	
	private static boolean doesYtFileExists(String value) {
        boolean result = false;
        for (String path : YoutubeFile) {
            File file = new File(path + "/" + value);
            result = file.exists();
            if (result) {
                Log.d(TAG, path + " contains su binary");
                break;
            }
        }
        return result;
    }
}
