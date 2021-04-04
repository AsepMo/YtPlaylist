package com.androweb.engine.app.utils;

import android.app.AlertDialog;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.List;
import java.util.*;

public class VideoUtils {

	public static final String MP4 = ".mp4", MP3 = ".mp3", JPG = ".jpg", JPEG = ".jpeg", PNG = ".png", DOC = ".doc", DOCX = ".docx", XLS = ".xls", XLSX = ".xlsx", PDF = ".pdf";
    public final static int 
		KILOBYTE = 1024,
		MEGABYTE = KILOBYTE * 1024,
		GIGABYTE = MEGABYTE * 1024,
		MAX_BYTE_SIZE = KILOBYTE / 2,
		MAX_KILOBYTE_SIZE = MEGABYTE / 2,
		MAX_MEGABYTE_SIZE = GIGABYTE / 2;
	

    public static void killAllProcessorServices(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            String processName = context.getPackageName() + ":service";
            if (next.processName.equals(processName)) {
                android.os.Process.killProcess(next.pid);
                break;
            }
        }
    }

    public static boolean isProcessorServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            String processName = context.getPackageName() + ":service";
            if (next.processName.equals(processName)) {
                return true;
            }
        }
        return false;
    }
	
	public static void openFile(Context c, File file)
	{
		if (file.isDirectory())
			throw new IllegalArgumentException("File cannot be a directory!");

		Intent intent = createFileOpenIntent(file);

		try
		{
			c.startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			c.startActivity(Intent.createChooser(intent, file.getName()));
		}
		catch (Exception e)
		{
			new AlertDialog.Builder(c)
				.setMessage(e.getMessage())
				//.setTitle(R.string.error)
				.setPositiveButton(android.R.string.ok, null)
				.show();
		}
	}
	
	public static Intent createFileOpenIntent(File file)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);		
		intent.setDataAndType(Uri.fromFile(file), getFileMimeType(file));
		return intent;
	}

	public static String getFileExtension(File file)
	{
		return getFileExtension(file.getName());
	}
	/**
	 * Gets extension of the file name excluding the . character
	 */
	public static String getFileExtension(String fileName)
	{
		if (fileName.contains("."))
			return fileName.substring(fileName.lastIndexOf('.')+1);
		else 
			return "";
	}

	public static String getFileMimeType(File file)
	{
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
		if (type == null) return "*/*";
		return type;
	}
	
	public static boolean videoExists(String directory) {
		File videoDir = new File(directory);
        if (videoDir.exists() && videoDir.isDirectory()) {
            File infoFile = new File(videoDir + "/info.json");
            if (infoFile.exists() && infoFile.isFile()) {
                VideoInfo sourceInfo = VideoInfo.getVideoInfo(infoFile);
                if (sourceInfo != null) {
                    return true;
                }
            }
        }
        return false;
    }
	

    public static boolean videoExists(File videoDir) {
        if (videoDir.exists() && videoDir.isDirectory()) {
            File infoFile = new File(videoDir + "/info.json");
            if (infoFile.exists() && infoFile.isFile()) {
                VideoInfo sourceInfo = VideoInfo.getVideoInfo(infoFile);
                if (sourceInfo != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static VideoInfo getVideoInfoFromVideoPath(File sourceDir) {
        if (sourceDir.isDirectory()) {
            File infoFile = new File(sourceDir + "/info.json");
            if (infoFile.exists() && infoFile.isFile()) {
                return VideoInfo.getVideoInfo(infoFile);
            }
        }
        return null;
    }

	public static String formatVideoSize(String file)
	{
		return formatVideoSize(file.length());		
	}
	
	public static String formatVideoSize(File file)
	{
		return formatVideoSize(file.length());		
	}

	public static String formatVideoSize(long size)
	{
		if (size < MAX_BYTE_SIZE)
			return String.format(Locale.ENGLISH, "%d bytes", size);
		else if (size < MAX_KILOBYTE_SIZE)
			return String.format(Locale.ENGLISH, "%.2f kb", (float)size / KILOBYTE);
		else if (size < MAX_MEGABYTE_SIZE)
			return String.format(Locale.ENGLISH, "%.2f mb", (float)size / MEGABYTE);
		else 
			return String.format(Locale.ENGLISH, "%.2f gb", (float)size / GIGABYTE);
	}

	public static String formatVideoSize(Collection<File> files)
	{
		return formatVideoSize(getVideoSize(files));
	}

	public static long getVideoSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getVideoSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }
	
	public static long getVideoSize(File... files)
	{
		if (files == null) return 0l;
		long size=0;
		for (File file : files)
		{
			if (file.isDirectory())
				size += getVideoSize(file.listFiles());
			else size += file.length();
		}
		return size;
	}

	public static long getVideoSize(Collection<File> files)
	{
		return getVideoSize(files.toArray(new File[files.size()]));
	}

}
