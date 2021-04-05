package com.youtube.playlist.adapters;

import com.youtube.playlist.R; 
import com.youtube.playlist.Playlist;
import com.youtube.playlist.utils.YoutubeInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import com.squareup.picasso.Picasso;
import com.youtube.playlist.PlaylistDetailsActivity;
import com.youtube.playlist.utils.TimeAgo;

public class YoutubeAdapter extends ArrayAdapter<Playlist>
{
	private ArrayList<Playlist> videoList;
	// Layout Inflater
    private final LayoutInflater inflater;
    private String key;
    private Activity activity;
    //private int REQ_PLAYER_CODE  = 1;
    private int textColor;

	public YoutubeAdapter(Activity activity, ArrayList<Playlist> videoList, String yt_key, int titleColor)
	{
        super(activity, R.layout.item_youtube_playlist, videoList);
		this.activity  = activity;
		this.inflater = LayoutInflater.from(activity);
        this.key = yt_key;
        this.videoList = videoList;
		this.textColor  = titleColor;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		// TODO: Implement this method
		final Playlist video = videoList.get(position);

		if (convertView == null)
		{
            convertView = this.inflater.inflate(R.layout.item_youtube_playlist, parent, false);
		}

		ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail_image_view);
		Picasso.with(activity)
			.load(video.getThumbnailUrl())
			.placeholder(R.drawable.video_placeholder)
			.into(thumbnail);

		ImageView icon = (ImageView) convertView.findViewById(R.id.imgFileIcon);
		Picasso.with(activity)
			.load(video.getThumbnailUrl())
			.placeholder(R.drawable.video_placeholder)
			.into(icon);

		TextView name = (TextView) convertView.findViewById(R.id.video_title_label);
		name.setText(video.getTitle());	
		name.setTextColor(textColor);
		TextView published = (TextView) convertView.findViewById(R.id.video_duration_label);       
		
		try {
            DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
            DateTime dt = parser.parseDateTime(video.getPublished());

            long videoSecs = (dt.getMillis())/1000;
            long nowSecs = (new Date().getTime())/1000;
            long secs = nowSecs - videoSecs;
            published.setText(TimeAgo.getTimeAgo(secs));
			
        } catch (Exception e) {
            e.printStackTrace();
            published.setText("NA");
        } 
		
		convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					final Playlist video = videoList.get(position);

					Intent videoIntent = new Intent(activity, PlaylistDetailsActivity.class);
					videoIntent.putExtra(video.TITLE , video.getTitle());
					videoIntent.putExtra(video.THUMBNAILS, video.getThumbnailUrl());
					videoIntent.putExtra(video.DESCRIPTIONS, video.getDescription());
					videoIntent.putExtra(video.VIDEOID, video.getVideoID());
					videoIntent.putExtra(video.IDENTIFIER, video.getIdentifier());
					activity.startActivity(videoIntent);
					YoutubeInfo info = new YoutubeInfo(activity, video.getTitle(), video.getThumbnailUrl() , video.getVideoID(), video.getDescription(), video.getPublished(), true);
					info.initialise(video);

				}
			});

		return convertView;
	}

}
