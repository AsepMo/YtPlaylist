package com.youtube.playlist;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeIntents;

import com.youtube.playlist.R;
import com.androweb.engine.graphics.typeface.CalligraphyContextWrapper; 
import com.androweb.engine.app.utils.AppsPackNames; 
import com.youtube.playlist.PlaylistActivity;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.analytics.YoutubeAnalytics;
import com.youtube.playlist.utils.YoutubeUtils;
import com.youtube.playlist.utils.YoutubeNoConnection;
import com.youtube.playlist.utils.YoutubeWebView;
import com.youtube.playlist.fragments.YoutubePlaylistFragment;
import com.youtube.playlist.downloader.HistoryDownloader;

public class YoutubePlaylistActivity extends AppCompatActivity 
{
	private Toolbar mToolbar;
	public static String TAG = YoutubePlaylistActivity.class.getSimpleName();

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;

	public static void start(final Context c, final long time)
	{
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					Intent mApplication = new Intent(c, YoutubePlaylistActivity.class);
					c.startActivity(mApplication);
				}
			}, time);
	}

	public YoutubePlaylistActivity setSubtitle(final String subTitle)
	{

		mToolbar.setSubtitle(subTitle);
		return this;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setNavigationIcon(R.drawable.btn_home_playlist);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
				@Override 
				public void onClick(View v)
				{
					YoutubePlaylistActivity.this.finish();
					PlaylistActivity.start(YoutubePlaylistActivity.this);
				}
			});
        setSupportActionBar(mToolbar);

		if (YoutubeUtils.isConnected(this))
			showUrl("youtube", YoutubeApi.YOUTUBE_BERANDA);
		else
			showUrl("noconnection", null);
    }

	public void showUrl(String requestFragment, String requestUrl)
	{   
		switch (requestFragment)
		{
			case "noconnection":
				Fragment dashboard = new YoutubeNoConnection();
				showFragment(dashboard);
				break;
			case "youtube":
				Fragment youtube = YoutubePlaylistFragment.createFor(requestUrl);
				showFragment(youtube);
				break;
			case "browser":
				YoutubeBrowserActivity.start(this, requestUrl);
				break;
			case "user":
				Intent user = YouTubeIntents.createUserIntent(this, requestUrl);
				startActivity(user);
				break;
			case "Search":
				Intent search = YouTubeIntents.createSearchIntent(this, requestUrl);
				startActivity(search);
				break;
			case "history":
				/*Intent mApplication = new Intent(YoutubeActivity.this, YoutubeHistoryDownload.class);
				 startActivity(mApplication);*/
				Fragment history = new HistoryDownloader();
				showFragment(history);
				break;	
		}
	}

	public void showFragment(Fragment developert)
	{
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, developert)
			.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		getMenuInflater().inflate(R.menu.menu_youtube_playlist, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch (item.getItemId())
		{
			case android.R.id.home:
				YoutubePlaylistActivity.this.finish();
				return true;
			case R.id.action_menu_search:
			    mToolbar.setSubtitle("Search");

				final EditText editSearch = new EditText(this);
				new AlertDialog.Builder(this)
					.setMessage("Youtube Search")
					.setView(editSearch)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					})
					.setPositiveButton("Ok", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							boolean Youtube = YoutubeAnalytics.isIntalled(YoutubePlaylistActivity.this, AppsPackNames.GOOGLE_YOUTUBE);
							if (Youtube)
							{
								showUrl("search", editSearch.getText().toString());
							}
							else
							{
								showUrl("browser", YoutubeApi.YOUTUBE_SEARCH + editSearch.getText().toString());				
							}
							dialog.dismiss();
						}
					}).show();
				return true;	
			case R.id.action_menu_channel:
			    mToolbar.setSubtitle("Channel");	
				boolean Youtube = YoutubeAnalytics.isIntalled(this, AppsPackNames.GOOGLE_YOUTUBE);
				if (Youtube)
				{
					showUrl("user", YoutubeApi.YOUTUBE_ASEPMO_USER_ID);
				}
				else
				{
					showUrl("browser", YoutubeApi.YOUTUBE_ASEPMO_CHANNEL_URL);
				}
				return true;	
			case R.id.action_beranda:
			    mToolbar.setSubtitle("Beranda");
				showUrl("youtube", YoutubeApi.YOUTUBE_BERANDA);
				return true;
			case R.id.action_tutorial:
			    mToolbar.setSubtitle("Tutorial");
				//showUrl("youtube", YoutubeApi.YOUTUBE_TUTORIAL_);
				new AlertDialog.Builder(this)
					.setMessage("Belum Ada Playlistnya..")
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					}).show();
				return true;
		 	case R.id.action_movie_horror:
			    mToolbar.setSubtitle("Horror");
				showUrl("youtube", YoutubeApi.YOUTUBE_MOVIE_HORROR);
				return true;
			case R.id.action_movie_comedy:
				mToolbar.setSubtitle("Comedy");
				showUrl("youtube", YoutubeApi.YOUTUBE_MOVIE_COMEDY);
				return true;
			case R.id.action_movie_animasi:
				mToolbar.setSubtitle("Animasi");
				showUrl("youtube", YoutubeApi.YOUTUBE_MOVIE_ANIMATION);
				return true;
			case R.id.action_music:
			    mToolbar.setSubtitle("Music");
				showUrl("youtube", YoutubeApi.YOUTUBE_MOVIE_COMEDY);
				return true;
			case R.id.action_cartoon:
				mToolbar.setSubtitle("Animation");
				showUrl("youtube", YoutubeApi.YOUTUBE_MUSIC_);
				return true;
			case R.id.action_tictoc:
				mToolbar.setSubtitle("TicToc");
				showUrl("youtube", YoutubeApi.YOUTUBE_TICTOC);
				return true;
			case R.id.action_tara:
			    mToolbar.setSubtitle("T-Ara");
				showUrl("youtube", YoutubeApi.YOUTUBE_GIRLBAND_TARA);
				return true;
			case R.id.action_street_fashion:
			    mToolbar.setSubtitle("Street Fashion");
				showUrl("youtube", YoutubeApi.YOUTUBE_STREET_FASHION);
				return true;
			
			case R.id.action_menu_browser:
			    mToolbar.setSubtitle("Youtube");				
				showUrl("browser", YoutubeApi.YOUTUBE_WEB_URL);
				return true;	

			case R.id.action_menu_history:
			    mToolbar.setSubtitle("");				
				showUrl("history", null);
				return true;	

			case R.id.action_menu_settings:
			    mToolbar.setSubtitle("Settings");				
				//showUrl("history", null);
				return true;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
	
		super.onBackPressed();
	}


	@Override
	protected void attachBaseContext(Context newBase)
	{
		// TODO: Implement this method
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
