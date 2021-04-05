package com.youtube.playlist.downloader;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;

import com.youtube.playlist.R;
import com.youtube.playlist.BuildConfig;
import com.androweb.engine.app.utils.VideoInfo;
import com.androweb.engine.app.utils.VideoUtils;
import com.youtube.playlist.folders.YoutubeFolder;
import com.androweb.engine.graphics.Thumbnail;
import com.androweb.engine.graphics.typeface.CalligraphyContextWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class YoutubeHistoryDownload extends AppCompatActivity
{
	private LinearLayout welcomeLayout;
	private ListView listView;
	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_loader_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("History Download");
		mToolbar.setTitleTextColor(Color.BLACK);
		//mToolbar.setNavigationIcon(R.drawable.ic_av_download);
		mToolbar.inflateMenu(R.menu.menu_history_downloader);
		mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
			{
				@Override
				public boolean onMenuItemClick(MenuItem item)
				{
					switch(item.getItemId())
					{
						case R.id.action_upload_video:
							/*Intent subActivity = new Intent(getActivity(), FolderMeActivity.class);
							 startActivity(subActivity);
							 getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);*/
							return true;

					}
					return true;
				}
			});
		setSupportActionBar(mToolbar);
		listView = (ListView)findViewById(R.id.history_list);
		View header = getLayoutInflater().inflate(R.layout.history_header_view, listView, false);
		listView.addHeaderView(header, null, false);

		welcomeLayout = (LinearLayout)findViewById(R.id.welcome_layout);


        HistoryLoader historyLoader = new HistoryLoader(YoutubeHistoryDownload.this, listView, new String[]{VideoUtils.MP4});
        historyLoader.execute();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		menu.add("Explore")
		.setIcon(R.drawable.icon_archive_explore)
		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				//FolderMeActivity.start(getActivity());
				return true;
			}
		}).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

    public void OpenAppListing(View v)
	{
		rerunHistoryLoader();
        /*Intent i = new Intent(getApplicationContext(), AppListing.class);
		 startActivity(i);
		 overridePendingTransition(R.anim.fadein, R.anim.fadeout);*/
    }

    public void OpenFilePicker(View v)
	{
		//Intent subActivity = new Intent(getActivity(), FolderMeActivity.class);
		//subActivity.putExtra("action", FolderMeActivity.Actions.SelectFile);
		//startActivity(subActivity); 
	    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void cleanOldSources()
	{
        File dir = new File(YoutubeFolder.ZFOLDER_YOUTUBE);
        if (dir.exists())
		{
            File[] files = dir.listFiles();
            for (File file : files)
			{
                if (!file.getName().equalsIgnoreCase("Download"))
				{
                    try
					{
                        FileUtils.cleanDirectory(file);
                        file.delete();
                    }
					catch (Exception e)
					{
                        Log.d("History", e.getMessage());
                    }
                }
            }
        }
		else
		{
            dir.mkdirs();
        }
    }

    @Override
    public void onResume()
	{
        super.onResume();
        rerunHistoryLoader();
    }

    private void rerunHistoryLoader()
	{
        HistoryLoader historyLoaderTwo = new HistoryLoader(YoutubeHistoryDownload.this, listView, new String[]{VideoUtils.MP4});
        historyLoaderTwo.execute();
    }

    private static class ViewHolder
	{
        TextView videoTitle;
        TextView videoSize;
        ImageView thumbnail;
        int position;
    }

    private class HistoryLoader extends AsyncTask<String, String, List<VideoInfo>>
	{

		private ListView listView;
		private List<VideoInfo> historyItems;
		private String[] extensions;
		private Context mContext;
		
		public HistoryLoader(Context context,ListView listView, String[] extensions)
		{
			this.mContext = context;
			this.listView = listView;
			this.extensions = extensions;
		}

        @Override
        protected void onPreExecute()
		{

        }

        @Override
        protected void onProgressUpdate(String... text)
		{

        }

		@Override
        protected List<VideoInfo> doInBackground(String... params)
		{

            historyItems = new ArrayList<>();
			
			File MP4 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
			if (MP4.exists())
			{
                listOfFile(MP4);
            }

            return historyItems;
        }

		private void listOfFile(File dir)
		{
			File[] list = dir.listFiles();

			for (File file : list)
			{
				if (file.isDirectory())
				{
					if (!new File(file, ".nomedia").exists() && !file.getName().startsWith("."))
					{
						Log.w("LOG", "IS DIR " + file);
						listOfFile(file);
					}
				}
				else
				{
					String path = file.getAbsolutePath();
					//String[] extensions = new String[]{".mp4"};
					for (String ext : extensions)
					{
						if (path.endsWith(ext))
						{
							VideoInfo videoInfo = new VideoInfo();

						    videoInfo.videoTitle = path;
							String[] split = path.split("/");
							videoInfo.videoTitle = split[split.length - 1];
						    videoInfo.videoThumb = path;
							videoInfo.videoSize = VideoUtils.formatVideoSize(file);
							historyItems.add(videoInfo);
							Log.i("LOG", "ADD " + videoInfo.videoTitle + " " + videoInfo.videoThumb);
						}
					}
				}
			}
			Log.d("LOG", historyItems.size() + " DONE");
		}

        @Override
        protected void onPostExecute(final List<VideoInfo> AllVideo)
		{
            if (AllVideo.size() < 1)
			{
				listView.setVisibility(View.GONE);
				welcomeLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				welcomeLayout.setVisibility(View.INVISIBLE);

				final ArrayAdapter<VideoInfo> videoAdapter = new ArrayAdapter<VideoInfo>(YoutubeHistoryDownload.this, R.layout.history_list_item, AllVideo) {
					@SuppressLint("InflateParams")
					@Override
					public View getView(final int position, View convertView, ViewGroup parent)
					{
						if (convertView == null)
						{
							convertView = getLayoutInflater().inflate(R.layout.history_list_item, null);
						}

						final VideoInfo pkg = getItem(position);

						ViewHolder holder = new ViewHolder();

						holder.videoTitle = (TextView) convertView.findViewById(R.id.history_item_label);
						holder.videoSize = (TextView) convertView.findViewById(R.id.history_item_package);
						holder.thumbnail = (ImageView) convertView.findViewById(R.id.history_item_icon);

						convertView.setTag(holder);

						holder.videoTitle.setText(pkg.getVideoTitle());
						holder.videoSize.setText(pkg.getVideoSize());
						Thumbnail.setThumbnail(pkg.videoThumb, MICRO_KIND, holder.thumbnail, false);
						convertView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
									final VideoInfo video = AllVideo.get(position);
									Intent mVideo = new Intent(Intent.ACTION_VIEW);
									mVideo.setDataAndType(Uri.parse(video.videoThumb), "video/*");
									startActivity(mVideo);
								}
							});
						return convertView;
					}
				};
				listView.setAdapter(videoAdapter);
				listView.setVisibility(View.VISIBLE);
			}
        }

    }

	@Override
	protected void attachBaseContext(Context newBase)
	{
		// TODO: Implement this method
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
