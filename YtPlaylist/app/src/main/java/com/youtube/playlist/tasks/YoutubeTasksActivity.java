package com.youtube.playlist.tasks;

import com.youtube.playlist.R; 
import com.youtube.playlist.Playlist; 
import com.youtube.playlist.PlaylistActivity;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.data.YoutubeData;
import com.youtube.playlist.services.YoutubeService;
import com.androweb.engine.graphics.typeface.CalligraphyContextWrapper; 

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.*;

import java.io.File;

public class YoutubeTasksActivity extends AppCompatActivity 
{

	public static String ACTION_ADD_PLAYLIST = "addPlaylist";
	public static String ADD_PLAYLIST = "playlistId";
	
	private String playlistID = YoutubeApi.YOUTUBE_BERANDA;
	public static ProgressBar serviceProgress;
	
    public static void start(final Context c) {

        Intent mApplication = new Intent(c, YoutubeTasksActivity.class);
        c.startActivity(mApplication);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_task);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		
		serviceProgress = (ProgressBar)findViewById(R.id.main_progress_bar);
		
		String action = getIntent().getAction();

        if (action != null && action.equals(ACTION_ADD_PLAYLIST))
		{
			String playlistID = getIntent().getStringExtra(ADD_PLAYLIST);
			YoutubeTask task = new YoutubeTask(this, playlistID);
			task.execute();			
		}
		else
		{
			File file = new File(YoutubeData.FOLDER + "/" + YoutubeData.FILENAME);

			if (file.exists())
			{
				Intent mYoutubeService =  new Intent(YoutubeTasksActivity.this, PlaylistActivity.class);
				startActivity(mYoutubeService);
				YoutubeTasksActivity.this.finish();
			}
			else
		    {			
				YoutubeTask task = new YoutubeTask(this, playlistID);
				task.execute();	
			}
		}
    }

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
	}

	@Override
	 protected void attachBaseContext(Context newBase)
	 {
	 // TODO: Implement this method
	 super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	 }
}
