package com.androweb.engine.app.utils;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Niranjan on 30-05-2015.
 */
public class VideoInfo {

    public String videoTitle = "";
    public String videoSize = "";
	public String videoThumb = "";
    public boolean hasVideo;

	public VideoInfo()
	{}
    public VideoInfo(String videoTitle,String videoThumb, String videoSize) {
        this.videoTitle = videoTitle;
		this.videoThumb = videoThumb;
	    this.videoSize = videoSize;
        this.hasVideo = true;
    }

    public VideoInfo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    /*public static void initialise(ProcessService processService) {
        try {
            JSONObject json = new JSONObject();
            json.put("title", processService.title);
            json.put("file_size", processService.fileSize);
            json.put("update", true);
            String filePath = processService.OutputDir + "/info.json";
            FileUtils.writeStringToFile(new File(filePath), json.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setjavaSourceStatus(ProcessService processService, Boolean status) {
        try {
            File infoFile = new File(processService.OutputDir + "/info.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            json.put("update", status);
            FileUtils.writeStringToFile(infoFile, json.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void delete(ProcessService processService) {
        try {
            File infoFile = new File(processService.OutputDir + "/info.json");
            infoFile.delete();
        } catch (Exception e) {
            Ln.e(e);
        }
    }*/

    public static String getTitle(String directory) {
        try {
            File infoFile = new File(directory + "/info.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("title");
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public static VideoInfo getVideoInfo(File infoFile) {
        try {
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            if (json.getBoolean("update")) {
                return new VideoInfo(json.getString("title"),json.getString("tumbnail"), json.getString("file_size"));
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public String getVideoTitle() {
        return videoTitle;
    }
	
    public String getVideoSize() {
        return videoSize;
    }

	public String getThumbnail() {
        return videoThumb;
    }
	
    public boolean hasVideo() {
        return hasVideo;
    }
}
