package com.youtube.playlist;

import com.youtube.playlist.R;
import com.youtube.playlist.PlaylistApplication;
import com.youtube.playlist.Playlist;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.player.YoutubePlayerActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;

import java.util.UUID;

import com.androweb.engine.widget.ScalingLayout;
import com.androweb.engine.widget.ScalingLayoutListener;
import com.androweb.engine.widget.state.State;
import com.squareup.picasso.Picasso;

public class PlaylistDetailsActivity extends AppCompatActivity
{
	private String YoutubeMe = "com.youtube.playist.PlaylistActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

		final String title = getIntent().getStringExtra(Playlist.TITLE);
		final String thumbnail = getIntent().getStringExtra(Playlist.THUMBNAILS);
		final String description = getIntent().getStringExtra(Playlist.DESCRIPTIONS);
		final String videoId = getIntent().getStringExtra(Playlist.VIDEOID);
		toolbar.setTitle(title);

		final ImageView cover = (ImageView)findViewById(R.id.imageView); 
		Picasso.with(this)
			.load(thumbnail) // Uri of the picture
			.into(cover);

		final TextView Title = (TextView)findViewById(R.id.title);
        Title.setText(title);

		final TextView descriptions = (TextView)findViewById(R.id.descriptions);
        descriptions.setText(description);


		final RelativeLayout watchNowLayout = (RelativeLayout)findViewById(R.id.preview);
		final ImageView watchNow = (ImageView)findViewById(R.id.watchNow);
		watchNowLayout.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					Intent Preview = new Intent(PlaylistDetailsActivity.this, YoutubePlayerActivity.class);
					Preview.putExtra(YoutubePlayerActivity.TAG_URL, videoId);
					startActivity(Preview);
					//Application.setNotification(YoutubeDetailsActivity.this, title, YoutubeApi.YOUTUBE_BERANDA);
				}
			});
			
		final RelativeLayout watchLaterLayout = (RelativeLayout)findViewById(R.id.download);
		final ImageView watchLater = (ImageView)findViewById(R.id.watchLater);
		watchLaterLayout.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YoutubeApi.Y2MATE_PAGE_URL + videoId));
					startActivity(browserIntent);
				}
			});
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch (item.getItemId())
		{
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null)
				{
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
		}
		return super.onOptionsItemSelected(item);
	}



}
