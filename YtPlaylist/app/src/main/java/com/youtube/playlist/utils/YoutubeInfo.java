package com.youtube.playlist.utils;

import com.youtube.playlist.R;
import com.youtube.playlist.Playlist; 
import com.youtube.playlist.folders.YoutubeFolder;
import com.youtube.playlist.player.VideoPlayerActivity;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class YoutubeInfo
{

	private static Activity activity;
    public String title;
    public String thumbnail_url;
    public String videoID;
	public String description;
	public String published;
	public boolean isUpdated;
	public static String TAG = YoutubeInfo.class.getSimpleName();
	public static String OutputDir = YoutubeFolder.ZFOLDER_YOUTUBE_ANALYTICS;

	public YoutubeInfo(Activity activity)
	{
		this.activity = activity;
	}
	
	public YoutubeInfo(Activity activity, String title, String videoId, boolean update)
	{
		this.activity = activity;
        this.title = title;
        this.videoID = videoId;
		this.isUpdated = update;
    }
	
    public YoutubeInfo(Activity activity, String title, String thumbnail_url, String videoid, String description, String published, boolean update)
	{
		this.activity = activity;
        this.title = title;
        this.thumbnail_url = thumbnail_url;
        this.videoID = videoid;
		this.description = description;
		this.published = published;
		this.isUpdated = update;
    }


    public YoutubeInfo(Activity activity, boolean isUpdated)
	{
		this.activity = activity;
        this.isUpdated = isUpdated;
    }

    public void initialise(Playlist video)
	{
        try
		{
            JSONObject json = new JSONObject();
            json.put("title", video.getTitle());
            json.put("thumbnails", video.getThumbnailUrl());
            json.put("videoId", video.getVideoID());
			json.put("description", video.getDescription());
			json.put("publishedAt", video.getPublished());
            json.put("update", video.getUpdater());
            String filePath = OutputDir + "/yt_initialise.json";
			File file = new File(filePath);
			//file.getParentFile().mkdirs();
            FileUtils.writeStringToFile(file, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

    public static void setStatus(Boolean status)
	{
        try
		{
            File infoFile = new File(OutputDir + "/status.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            json.put("update", status);
            FileUtils.writeStringToFile(infoFile, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static void setVideoPlay(Activity c, String title, String videoUrl)
	{
		
        try
		{
            File infoFile = new File(OutputDir + "/yt_video.json");
            JSONObject json = new JSONObject();
            json.put("videoUrl", videoUrl);
			
			File dlCacheFile = new File(c.getCacheDir().getAbsolutePath() + "/" + videoUrl);
			try {
				dlCacheFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
            FileUtils.writeStringToFile(infoFile, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }
	
    public static void setVideoPlay(Activity c, String title, String videoUrl, boolean update)
	{
		Intent videoPlay = new Intent(c, VideoPlayerActivity.class);
		//Intent videoPlay = new Intent(Intent.ACTION_VIEW);
		//videoPlay.setDataAndType(Uri.parse(videoUrl), "video/*");
		videoPlay.putExtra(VideoPlayerActivity.TAG_URL, videoUrl);
		c.startActivity(videoPlay);
		
        try
		{
            File infoFile = new File(OutputDir + "/yt_video.json");
            JSONObject json = new JSONObject();
            json.put("title", title);
			json.put("videoUrl", videoUrl);
			json.put("update", update);
			
			File dlCacheFile = new File(c.getCacheDir().getAbsolutePath() + "/" + videoUrl);
			try {
				dlCacheFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
            FileUtils.writeStringToFile(infoFile, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static void setAudioPlay(Activity c, String title, String audioUrl, boolean update)
	{
		Intent videoPlay = new Intent(Intent.ACTION_VIEW);
		videoPlay.setDataAndType(Uri.parse(audioUrl), "audio/*");
		//videoPlay.putExtra(VideoPlayerActivity.TAG_URL, videoUrl);
		c.startActivity(videoPlay);
	
        try
		{
            File infoFile = new File(OutputDir + "/yt_audio.json");
            JSONObject json = new JSONObject();
            json.put("title", title);
			json.put("audio", audioUrl);
			json.put("update", update);

			File dlCacheFile = new File(c.getCacheDir().getAbsolutePath() + "/" + infoFile.getName());
			try {
				dlCacheFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
            FileUtils.writeStringToFile(infoFile, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }
	
    public static void delete(String file)
	{
        try
		{
            File infoFile = new File(OutputDir + File.separator + file);
            infoFile.delete();
        }
		catch (Exception e)
		{
            Log.e(TAG, e.getMessage());
        }
    }
	
	public static String getVideoTitle()
	{
		try
		{
            File infoFile = new File(OutputDir + "/yt_video.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("title");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }    
	}
	
	public static String getVideoPlay()
	{
		try
		{
            File infoFile = new File(OutputDir + "/yt_video.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("videoUrl");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }    
	}

	public static String getAudioTitle()
	{
		try
		{
            File infoFile = new File(OutputDir + "/yt_video.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("title");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }    
	}
	
	public static String getAudioPlay()
	{
		try
		{
            File infoFile = new File(OutputDir + "/yt_audio.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("audioUrl");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }    
	}
	
    public static String getYoutubeTitle()
	{
        try
		{
            File infoFile = new File(OutputDir + "/yt_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("title");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }

	public static String getYoutubeThumbnail() 
	{
		File infoFile = new File(OutputDir + "default.png");
		return infoFile.getAbsolutePath();
    }

	public static String getYoutubeThumbnailUrl()
	{
        try
		{
            File infoFile = new File(OutputDir + "/yt_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("thumbnails");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }

	public static String getYoutubeVideoId()
	{
        try
		{
            File infoFile = new File(OutputDir + "/yt_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("videoId");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }

	public static String getYoutubeDescription()
	{
        try
		{
            File infoFile = new File(OutputDir + "/yt_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("description");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }
	
	public static String getYoutubePublished()
	{
        try
		{
            File infoFile = new File(OutputDir + "/yt_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("publishedAt");
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }
	
	public static YoutubeInfo getYoutubePlaylist(Activity activity)
	{
        try
		{
            JSONObject json = new JSONObject(FileUtils.readFileToString(new File(OutputDir + "/yt_playlist.json")));
            if (json.getBoolean("update"))
			{
                return new YoutubeInfo(activity, json.getString("title"), json.getString("videoId"), json.getBoolean("update"));
            }
			else
			{
                return null;
            }
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }
	

    public static YoutubeInfo getYoutubeInfo(Activity c, File file)
	{
        try
		{
            JSONObject json = new JSONObject(FileUtils.readFileToString(file));
            if (json.getBoolean("update"))
			{
                return new YoutubeInfo(c, json.getString("title"), json.getString("thumbnails"), json.getString("videoId"), json.getString("description"), json.getString("publishedAt"), json.getBoolean("update"));
            }
			else
			{
                return null;
            }
        }
		catch (IOException | JSONException e)
		{
            return null;
        }
    }
	
	// Recording Start/Stop
    //TODO: recording pause
    /*public static void onRecord(Activity c, boolean start){

        Intent intent = new Intent(c, RecordingService.class);
        if (start) {
            File folder = new File(FolderMe.ZFOLDER_AUDIO_RECORDER);
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }

            //start RecordingService
            c.startService(intent);
            //keep screen on while recording
            //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } else {     
			c.stopService(intent);
            //allow the screen to turn off again once recording is finished
            //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
	
	public static void stopRecording(Activity c){

        Intent intent = new Intent(c, RecordingService.class);
		c.stopService(intent);
	}*/
	
    public String getTitle()
	{
        return title;
    }

    public String getThumbnailUrl()
	{
        return thumbnail_url;
    }

    public String getVideoID()
	{
        return videoID;
    }
	
	public String getDescription()
	{
        return description;
    }

	public String getPublished()
	{
		return published;
	}

    public boolean hasVideo()
	{
        return isUpdated;
    }
}
