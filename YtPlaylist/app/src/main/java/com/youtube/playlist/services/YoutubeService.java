package com.youtube.playlist.services;

import com.youtube.playlist.Playlist;

import java.io.File;
import java.io.IOException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.Handler;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.youtube.playlist.tasks.YoutubeTask;


public class YoutubeService extends IntentService
{

	public static final String ACTION_DO_SOMETHING = 
	"com.youtube.playlist.background.ACTION_DO_SOMETHING";
	public static final String ACTION_UPDATE = 
	"com.youtube.playlist.background.ACTION_UPDATE";
	public static final String ACTION_PROGRESS = 
	"com.youtube.playlist.background.ACTION_PROGRESS";
    public static final String ACTION_ADD_PLAYLIST = "com.youtube.playlist.background.ACTION_ADD_PLAYLIST";

	public static final String EXTRA_UPDATE_TEXT = "UPDATE_TEXT";
	public static final String EXTRA_PROGRESS = "PROGRESS";
	public static final String EXTRA_MAX = "MAX";

	public static void start(Context c)
	{
		Intent service = new Intent(YoutubeService.ACTION_DO_SOMETHING);
		//service.putExtra("AsepMo", "AsepMo");
		c.startService(service);
	}

	public YoutubeService()
	{
		super("AndroWebService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		if (intent.getAction().equals(ACTION_DO_SOMETHING))
		{
			String playlistID = intent.getStringExtra("playlistId");
			
			doSomething(playlistID);			
		}
		else if (intent.getAction().equals(ACTION_ADD_PLAYLIST))
		{
			String playlistID = intent.getStringExtra("playlistId");
			YoutubeTask task = new YoutubeTask(this, playlistID);
			task.execute();			

		}
	}

	private void doSomething(final String playlistId)
	{
		for (int i = 0; i < 5; i++)
		{
			Intent progressIntent = new Intent(ACTION_PROGRESS);
			progressIntent.putExtra(EXTRA_PROGRESS, i);
			progressIntent.putExtra(EXTRA_MAX, 4);
			sendBroadcast(progressIntent);
			try
			{
				Thread.sleep(2000);
				//new Playlist(this).sendShortMessage(playlistId);
			}
			catch ( InterruptedException e )
			{
			}
		}
		Intent updateIntent = new Intent(ACTION_UPDATE);
		updateIntent.putExtra(EXTRA_UPDATE_TEXT,  "Start Activity");
		sendBroadcast(updateIntent);
	}
}
