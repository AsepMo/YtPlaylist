package com.youtube.playlist;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.os.Environment;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import com.google.android.youtube.player.YouTubeIntents;
import com.androweb.engine.app.utils.AppsPackNames;
import com.androweb.engine.graphics.typeface.CalligraphyContextWrapper; 

import com.youtube.playlist.R;
import com.youtube.playlist.utils.BrowserUtils;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.analytics.YoutubeAnalytics;
import com.youtube.playlist.data.YoutubeData; 
import com.youtube.playlist.tasks.YoutubeTasksActivity;
import com.youtube.playlist.player.YoutubePlayerActivity;
import com.youtube.playlist.YoutubePlaylistActivity; 
import com.youtube.playlist.adapters.YoutubeAdapter; 
import com.youtube.playlist.utils.YoutubeUtils;
import com.youtube.playlist.downloader.YoutubeHistoryDownload;
import com.youtube.playlist.downloader.YoutubeThumbnailsDownloader;
import com.youtube.playlist.downloader.HistoryDownloader;
 
import com.youtube.playlist.logger.YoutubeLogger;

public class PlaylistActivity extends AppCompatActivity {
	public static void start(final Context c) {

        Intent mApplication = new Intent(c, PlaylistActivity.class);
		mApplication.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(mApplication);
	}


	private Toolbar mToolbar;
	public static String TAG = PlaylistActivity.class.getSimpleName();
	private ListView mYoutubePlaylist;
	private ArrayList<Playlist> ytPlaylist;
	private YoutubeData data;
	private YoutubeAdapter mYoutubeAdapter;
	private int titleColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtube_playlist);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		
        setSupportActionBar(mToolbar);


		mYoutubePlaylist = (ListView)findViewById(R.id.youtubePlaylist);
		mYoutubePlaylist.setFastScrollEnabled(true);

		data = new YoutubeData(this, YoutubeData.FILENAME);	
		ytPlaylist = getLocallyStoredData();

		titleColor = Color.parseColor("#000000");
		mYoutubeAdapter = new YoutubeAdapter(this, ytPlaylist, YoutubeApi.DEVELOPER_KEY, titleColor);
		mYoutubePlaylist.setAdapter(mYoutubeAdapter);
		if (YoutubeUtils.isConnected(this)) {
			YoutubeLogger.sendBroadcast(this, "Youtube Is Running");
		} else {
			NoConnectionActivity.start(this, 0);
			PlaylistActivity.this.finish();
		}
	}

	public ArrayList<Playlist> getLocallyStoredData() {
        ArrayList<Playlist> items = null;
        try {
            items = data.loadFromFile();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

	public void setPlaylist(String playlistId) {
		Intent AddMe = new Intent(PlaylistActivity.this, YoutubeTasksActivity.class);
		AddMe.setAction(YoutubeTasksActivity.ACTION_ADD_PLAYLIST);
		AddMe.putExtra(YoutubeTasksActivity.ADD_PLAYLIST, playlistId);
		startActivity(AddMe);
		PlaylistActivity.this.finish();

	} 

	public void showUrl(String requestFragment, String requestUrl) {   
		switch (requestFragment) {

			case "youtube":
				setPlaylist(requestUrl);
				break;
			case "browser":
				Intent Browser = new Intent(PlaylistActivity.this, YoutubeBrowserActivity.class);
				Browser.putExtra(YoutubeBrowserActivity.EXTRA_URL, requestUrl); 
				startActivity(Browser);
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
				Intent mApplication = new Intent(PlaylistActivity.this, YoutubeHistoryDownload.class);
			    startActivity(mApplication);
				break;	
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		// TODO: Implement this method
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		menu.add("Add")
		    .setIcon(R.drawable.btn_add)
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
			{
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					new AlertDialog.Builder(PlaylistActivity.this)
						.setItems(new CharSequence[]{"Add VideoId","Add Playlist"}, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface  dialog, int which) {
								switch (which) {
									case 0:
										addVideoId();
										break;
									case 1:
									    addPlaylist();
										break;
								}
								dialog.dismiss();
							}
						})
						.show();
					return true;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		getMenuInflater().inflate(R.menu.menu_youtube, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case android.R.id.home:
				//YoutubeActivity.this.setPlaylist(YoutubeApi.YOUTUBE_BERANDA);
				return true;
			case R.id.action_menu_browser:
				showUrl("browser", YoutubeApi.YOUTUBE_WEB_URL);
				return true;	
			case R.id.action_menu_playlist:
			    YoutubePlaylistActivity.start(this, 0);
				return true;	
			case R.id.action_menu_downloader:
			    new AlertDialog.Builder(PlaylistActivity.this)
					.setTitle("Yt Download : ")
					.setItems(new CharSequence[]{"Thumbnails","Mp3", "Mp4"}, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface  dialog, int which) {
							switch (which) {
								case 0:
									addDownloaderThumbnails();
								   	break;
								case 1:
									addDownloaderAudio();
									break;
								case 2:
									addDownloaderVideo();
									break;
							}
							dialog.dismiss();
						}
					})
					.show();
				return true;
			case R.id.action_menu_history:
			   	showUrl("history", null);
				return true;	
			case R.id.action_menu_settings:
			   	YoutubeSettingsActivity.start(PlaylistActivity.this, 0);
				return true;	
		}
		return super.onOptionsItemSelected(item);
	}

	public void addVideoId() {
		final EditText edit = new EditText(this);
		edit.setHint("Url or VideoId here");
		new AlertDialog.Builder(this)
			.setView(edit)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) { 
					if (edit.getText().toString() != null && (edit.getText().toString().contains("://youtu.be/") || edit.getText().toString().contains("youtube.com/watch?v="))) {
						String text = getVideoIdFromYoutubeUrl(edit.getText().toString());
						Intent Preview = new Intent(PlaylistActivity.this, YoutubePlayerActivity.class);
						Preview.putExtra(YoutubePlayerActivity.TAG_URL, text);
						startActivity(Preview);		
					} else {
						Intent Preview = new Intent(PlaylistActivity.this, YoutubePlayerActivity.class);
						Preview.putExtra(YoutubePlayerActivity.TAG_URL, edit.getText().toString());
						startActivity(Preview);
					}

					dialog.dismiss();
				}
			})
			.show();
	}

	public void addPlaylist() {
		final EditText edit = new EditText(this);
		edit.setHint("Url or PlaylistId here");
		new AlertDialog.Builder(this)
			.setView(edit)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					if (edit.getText().toString() != null && (edit.getText().toString().contains("youtube.com/playlist?list="))) {

						final String videoID;

						if (edit.getText().toString().contains("youtube.com/playlist?list=")) {
							String[] videoURLSplit = edit.getText().toString().split("=");
							String[] videoURLSplit_playlist_or_extension = videoURLSplit[1].split("&");
							videoID = videoURLSplit_playlist_or_extension[0];
						} else {
							Uri videouri = Uri.parse(edit.getText().toString());
							videoID = videouri.getLastPathSegment();
    					}

						new Thread(new Runnable() {
								@Override
								public void run() { 
									Intent AddMe = new Intent(PlaylistActivity.this, YoutubeTasksActivity.class);
									AddMe.setAction(YoutubeTasksActivity.ACTION_ADD_PLAYLIST);
									AddMe.putExtra(YoutubeTasksActivity.ADD_PLAYLIST, videoID);
									startActivity(AddMe);
									PlaylistActivity.this.finish();
								}
							}).start();

					} else {
						Intent AddMe = new Intent(PlaylistActivity.this, YoutubeTasksActivity.class);
						AddMe.setAction(YoutubeTasksActivity.ACTION_ADD_PLAYLIST);
						AddMe.putExtra(YoutubeTasksActivity.ADD_PLAYLIST, edit.getText().toString());
						startActivity(AddMe);
						PlaylistActivity.this.finish();
					}
					dialog.dismiss();
				}
			})
			.show();
	}

	public void addDownloaderThumbnails() {
		final EditText edit = new EditText(this);
		edit.setHint("Url or VideoId here");
		new AlertDialog.Builder(this)
			.setView(edit)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					if (edit.getText().toString() != null && (edit.getText().toString().contains("://youtu.be/") || edit.getText().toString().contains("youtube.com/watch?v="))) {
						String text = getVideoIdFromYoutubeUrl(edit.getText().toString());
						YoutubeThumbnailsDownloader.start(PlaylistActivity.this, text);		
					} else {
						YoutubeThumbnailsDownloader.start(PlaylistActivity.this, edit.getText().toString());	
					}
					dialog.dismiss();
				}
			})
			.show();
	}

	public void addDownloaderAudio() {
		final EditText edit = new EditText(this);
		edit.setHint("Url or VideoId here");
		new AlertDialog.Builder(this)
			.setView(edit)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					if (edit.getText().toString() != null && (edit.getText().toString().contains("://youtu.be/") || edit.getText().toString().contains("youtube.com/watch?v="))) {
						String text = getVideoIdFromYoutubeUrl(edit.getText().toString());
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("https://www.y2mate.com/id/youtube-mp3/");
						stringBuilder.append(text);
						Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
						startActivity(intent);
					} else {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("https://www.y2mate.com/id/youtube-mp3/");
						stringBuilder.append(edit.getText().toString());
						Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
						startActivity(intent);
					}
					dialog.dismiss();
				}
			})
			.show();
	}

	public void addDownloaderVideo() {
		final EditText edit = new EditText(this);
		edit.setHint("Url or VideoId here");
		new AlertDialog.Builder(this)
			.setView(edit)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface  dialog, int which) {
					if (edit.getText().toString() != null && (edit.getText().toString().contains("://youtu.be/") || edit.getText().toString().contains("youtube.com/watch?v="))) {
						String text = getVideoIdFromYoutubeUrl(edit.getText().toString());
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("https://y2mate.com/youtube/");
						stringBuilder.append(text);
						stringBuilder.append("/?source=android-app");
						Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
						startActivity(intent);
					} else {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("https://y2mate.com/youtube/");
						stringBuilder.append(edit.getText().toString());
						stringBuilder.append("/?source=android-app");
						Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
						startActivity(intent);
					}
					dialog.dismiss();
				}
			})
			.show();
	}

	public static String getVideoIdFromYoutubeUrl(String string2) {
        Matcher matcher = Pattern.compile((String)"http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)", 2).matcher(string2);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
