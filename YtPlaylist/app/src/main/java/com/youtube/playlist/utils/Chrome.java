package com.youtube.playlist.utils;

import android.app.AlertDialog; 
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Intent; 

import com.youtube.playlist.Playlist;
import com.youtube.playlist.player.YoutubePlayerActivity;
import com.youtube.playlist.tasks.YoutubeTasksActivity;

public class Chrome
{ 
	public static void setOnClickListener(final Context context,final AdvancedWebView webView, final String title, final String url)
	{
		new AlertDialog.Builder(context)
			.setNegativeButton("Preview", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Intent Preview = new Intent(context, YoutubePlayerActivity.class);
					Preview.putExtra(YoutubePlayerActivity.TAG_URL, url);
					context.startActivity(Preview);
					
					dialog.dismiss();
				}
			}).setNeutralButton("Add Playlist", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Intent AddMe = new Intent(context, YoutubeTasksActivity.class);
					AddMe.setAction(YoutubeTasksActivity.ACTION_ADD_PLAYLIST);
					AddMe.putExtra(YoutubeTasksActivity.ADD_PLAYLIST, url);
					context.startActivity(AddMe);
					
					dialog.dismiss();
				}
			}).setPositiveButton("Download", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,  int which)
				{
					BrowserUtils.compileVideoId(webView, url);
					dialog.dismiss();
				}
			}).show();
	}
}
