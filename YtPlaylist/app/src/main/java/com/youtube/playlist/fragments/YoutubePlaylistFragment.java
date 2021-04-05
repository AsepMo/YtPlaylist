package com.youtube.playlist.fragments;

import com.youtube.playlist.R;
import com.youtube.playlist.PlaylistStarter;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.adapters.YoutubePlaylistAdapter;
import com.youtube.playlist.Playlist;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.youtube.playlist.analytics.YoutubeAnalytics;

public class YoutubePlaylistFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

	public static String TAG = YoutubePlaylistFragment.class.getSimpleName();
	public static final String EXTRA_URL = "playlist";
	
	public static YoutubePlaylistFragment createFor(String playlistId)
	{
        YoutubePlaylistFragment fragment = new YoutubePlaylistFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_URL, playlistId);
        fragment.setArguments(args);
        return fragment;
    }
	private View thisScreensView;
    private List<Playlist> displaylistArray = new ArrayList<Playlist>();
    private ListView mYoutubePlaylist = null;
	private SwipeRefreshLayout mRefresh;
    private YoutubePlaylistAdapter mYoutubePlaylistAdapter;
	private boolean ASWP_PBAR = true;

    String loadMsg;
    String loadTitle;
	String playlistId;
	int titleColor;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
		
		Log.d(TAG, "Fragment created");
	}
	
    //onCreateView...
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{


        //show life-cycle event in LogCat console...
		//context = getActivity().getApplicationContext();
        thisScreensView = inflater.inflate(R.layout.fragment_youtube_playlist, container, false);
		//playlist_id = "PLiKkX4KV1eFLUxsoE7fIDx5RDSC0qOdC4";
        playlistId = getArguments().getString(EXTRA_URL);

		
		loadTitle = "Loading...";
        loadMsg = "Loading your videos...";
		titleColor = Color.parseColor("#000000");
        //browserKey = "AIzaSyAYgHbHDXV1x-wSdJPqdPiwq-2GgdWEqWk";


        mYoutubePlaylist = (ListView) thisScreensView.findViewById(android.R.id.list);
		displaylistArray = new ArrayList<Playlist>();
		mRefresh = (SwipeRefreshLayout) thisScreensView.findViewById(R.id.swipeRefresh);
		
        //return the layout file as the view for this screen..
        return thisScreensView;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);

		//String title = YoutubeInfo.getYoutubeTitle();
        //getListView().setBackgroundColor(recyclerColor);
	    mYoutubePlaylistAdapter = new YoutubePlaylistAdapter(getActivity(), displaylistArray, YoutubeApi.DEVELOPER_KEY, titleColor);
        mYoutubePlaylist.setAdapter(mYoutubePlaylistAdapter);
		//mYoutubePlaylist.setFastScrollAlwaysVisible(true);
		mYoutubePlaylist.setVisibility(View.VISIBLE);
		
        mYoutubePlaylistAdapter.notifyDataSetChanged();

		mRefresh.setOnRefreshListener(this);
        mRefresh.setColorSchemeResources(R.color.colorAccent,
										 android.R.color.holo_green_dark,
										 android.R.color.holo_orange_dark,
										 android.R.color.holo_blue_dark);
		new TheTask().execute();
		
		//Refresh Playlist										   
       	if (mRefresh.isRefreshing())
			displaylistArray.clear();

		Log.d(TAG, "Refreshing");
		
	}

	

	@Override
	public void onResume()
	{
		// TODO: Implement this method
		super.onResume();
	}

	public void refresh()
	{
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run()
			{
				mRefresh.setRefreshing(false);
				new TheTask().execute();
			}
		},5000);
	}
	
	@Override
	public void onRefresh()
	{
		// TODO: Implement this method
		refresh();
	}

    private class TheTask extends AsyncTask<Void, Float, Void>
    {

        private Playlist displaylist;
        private ProgressBar progressBar;
        @Override
        protected void onPreExecute()
		{
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressBar = (ProgressBar)thisScreensView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

		@Override
		protected void onProgressUpdate(Float[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			float progress = values[0];
			progressBar.setMax(100);
			progressBar.setProgress((int) (progress * 100));
		}

        @Override
        protected Void doInBackground(Void... params)
		{
            // TODO Auto-generated method stub

            try
            {

                String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + playlistId + "&key=" + YoutubeApi.DEVELOPER_KEY + "&maxResults=50";

                String response = getUrlString(url);

                JSONObject json = new JSONObject(response.toString());

                JSONArray jsonArray = json.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++)
				{
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    JSONObject video = jsonObject.getJSONObject("snippet").getJSONObject("resourceId");
                    String title = jsonObject.getJSONObject("snippet").getString("title");

                    String id = video.getString("videoId");

                    String thumbUrl = jsonObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");

					String description = jsonObject.getJSONObject("snippet").getString("description");
					
					String published = jsonObject.getJSONObject("snippet").getString("publishedAt");
					if (ASWP_PBAR) {
						progressBar.setProgress(i);
						if (i == 100) {
							progressBar.setProgress(0);
						}
					}
					displaylist = new Playlist(title, thumbUrl , id, description, published, true);
					displaylistArray.add(displaylist);
					//AnalyticsManager.YoutubeLog(title, thumbUrl, id, published);	
		       }
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void result)
		{
            // TODO Auto-generated method stub
            super.onPostExecute(result);
			
		    mYoutubePlaylistAdapter.notifyDataSetChanged();
			progressBar.setVisibility(View.INVISIBLE);
        }

    }



    private byte[] getUrlBytes(String urlSpec) throws IOException
	{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try
		{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
                throw new IOException(connection.getResponseMessage() +
									  ": with " +
									  urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
			{
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
		finally
		{
            connection.disconnect();
        }
    }
    private String getUrlString(String urlSpec) throws IOException
	{
        return new String(getUrlBytes(urlSpec));
    }
}
