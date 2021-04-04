package com.youtube.playlist;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.youtube.playlist.logger.YoutubeLogger;
import com.youtube.playlist.folders.YoutubeFolder;
import com.androweb.engine.graphics.typeface.CalligraphyConfig;

public class YtPlaylistStarter extends AppCompatActivity {
    
    public static final String TAG = "YtPlaylistStarter";
    
    public static final String FILENAME = "youtube.json";
    public static final String SHARED_PREF_DATA_SET_CHANGED = ".datasetchanged";
    public static final String CHANGE_OCCURED = ".changeoccured";
    public static int mTheme = -1;
    public static String theme = "name_of_the_theme";
    public static final String THEME_PREFERENCES = ".themepref";
    public static final String RECREATE_ACTIVITY = ".recreateactivity";
    public static final String EXTRA_RECREATE = "recreate";
    public static final String THEME_SAVED = ".savedtheme";
    public static final String DARKTHEME = ".darktheme";
    public static final String LIGHTTHEME = ".lighttheme";
    private Handler mHandler = new Handler(); 
    private Runnable mRunner = new Runnable()
    {
        @Override 
        public void run()
        {
            YtPlaylistActivity.start(YtPlaylistStarter.this);
            YtPlaylistStarter.this.finish(); 
            YoutubeLogger.sendBroadcast(getApplicationContext(), "AWeb Is Running!");
            YoutubeFolder.initFolder(new YoutubeFolder.Builder(getApplicationContext())
                                     .setDefaultFolder(YoutubeFolder.ZFOLDER)
                                     .setFolderApk(true) 
                                     .setFolderImage(true)
                                     .setFolderAudio(true)
                                     .setFolderArchive(true)
                                     .setFolderEbook(true)
                                     .setFolderImage(true)
                                     .setFolderVideo(true)
                                     .setFolderYoutube(true)
                                     .setFolderScriptMe(true)
                                     .build()); 
        }
    }; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method 
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mHandler.postDelayed(mRunner, 2500);
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        // TODO: Implement this method
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO: Implement this method
        menu.add("Youtube")
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                   return true;
                }
            })
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause()
    {
        // TODO: Implement this method
        super.onPause(); 
        mHandler.removeCallbacks(mRunner);
    }

    @Override
    protected void onDestroy()
    {
        // TODO: Implement this method
        super.onDestroy();
        mHandler.removeCallbacks(mRunner);
    }
	
}
