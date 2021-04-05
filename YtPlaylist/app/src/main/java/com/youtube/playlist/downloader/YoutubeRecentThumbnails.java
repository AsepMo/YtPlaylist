package com.youtube.playlist.downloader;

import com.youtube.playlist.R;
import com.youtube.playlist.folders.YoutubeFolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.youtube.playlist.downloader.adapters.YoutubeThumbnailsAdapter;

public class YoutubeRecentThumbnails extends AppCompatActivity {


    public static ArrayList<File> thumbnailfiles;
    public static ArrayList<Bitmap> thumbnailbitmaps;

    RecyclerView recyclerview;
    RecyclerView.Adapter rvAdapter;
    RecyclerView.LayoutManager rvLayoutManager;

    public static TextView infotextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_thumbnails);

        infotextView = (TextView) findViewById(R.id.textView_info);
        preparethumbnailimages();
        recyclerviewsetup();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private void preparethumbnailimages() {
        thumbnailfiles = new ArrayList<>();
        thumbnailbitmaps = new ArrayList<>();
        File ordner = new File(YoutubeFolder.ZFOLDER_IMG, "Thumbnails");
        if(!ordner.exists()){
            ordner.mkdirs();
        }

        thumbnailfiles.addAll(Arrays.asList(ordner.listFiles()));
        if(thumbnailfiles.size() == 0){
            infotextView.setVisibility(View.VISIBLE);
        }

        int counter = 0;
        while (counter < thumbnailfiles.size()){
            thumbnailbitmaps.add(prepareBitmaps(thumbnailfiles.get(counter)));
            counter++;

            if(counter == thumbnailfiles.size()){
                Collections.reverse(thumbnailbitmaps);
                Collections.reverse(thumbnailfiles);
            }
        }
    }
    private Bitmap prepareBitmaps (File thumbnailfile){
        Bitmap bitmap;
        if(thumbnailfile.getAbsolutePath().contains("lowRes")) {
            bitmap = BitmapFactory.decodeFile(thumbnailfile.getAbsolutePath());
        } else {
            BitmapFactory.Options bitmapoptions = new BitmapFactory.Options();
            bitmapoptions.inSampleSize = 4;
            bitmap = BitmapFactory.decodeFile(thumbnailfile.getAbsolutePath(), bitmapoptions);
        }

        return bitmap;
    }

    private void recyclerviewsetup() {
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        rvLayoutManager = new GridLayoutManager(this, 3);
        recyclerview.setLayoutManager(rvLayoutManager);
        rvAdapter = new YoutubeThumbnailsAdapter();
        recyclerview.setAdapter(rvAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
