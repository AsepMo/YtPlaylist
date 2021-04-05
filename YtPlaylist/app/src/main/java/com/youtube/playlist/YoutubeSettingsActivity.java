package com.youtube.playlist;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.youtube.playlist.R;
import com.androweb.engine.graphics.typeface.CalligraphyContextWrapper; 
import com.youtube.playlist.utils.YoutubeNoConnection;

public class YoutubeSettingsActivity extends AppCompatActivity
{
	public static void start(final Context c, final long time)
	{
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					Intent mApplication = new Intent(c, YoutubeSettingsActivity.class);
					c.startActivity(mApplication);
				}
			}, time);
	}

	private Toolbar mToolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, new YoutubeNoConnection())
			.commit();
	}
	
		@Override
	protected void attachBaseContext(Context newBase)
	{
		// TODO: Implement this method
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

}
