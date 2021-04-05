package com.youtube.playlist.tasks;

import com.youtube.playlist.Playlist;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.data.YoutubeData;
import com.youtube.playlist.data.YoutubeDataBase;
import com.youtube.playlist.services.YoutubeService;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.youtube.playlist.PlaylistActivity;
import android.view.View;

public class YoutubeTask extends AsyncTask<String, Float, ArrayList<Playlist>>
{
	private Playlist displaylist;
	private YoutubeData storeData;
	private YoutubeDataBase mData;
	
	private ArrayList<Playlist> displaylistArray;
	//private ProgressDialog progressDialog;
	private Context mContext;
	private String playlistId;

	public YoutubeTask(Context context, String playlistId)
	{
		this.mContext = context;
		this.playlistId = playlistId;
		this.storeData = new YoutubeData(mContext, YoutubeData.FILENAME);
	}

	@Override
	protected void onPreExecute()
	{
		// TODO Auto-generated method stub
		super.onPreExecute();
		YoutubeTasksActivity.serviceProgress.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onProgressUpdate(Float[] values)
	{
		// TODO: Implement this method
		super.onProgressUpdate(values);
		YoutubeTasksActivity.serviceProgress.setMax(100);
		
		
	}

	@Override
	protected ArrayList<Playlist> doInBackground(String... params)
	{
		// TODO Auto-generated method stub
		displaylistArray = new ArrayList<Playlist>();
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

				displaylist = new Playlist(title, thumbUrl , id, description, published, true);
				//mData = new YoutubeDataBase(mContext);
				//mData.insertData(title, thumbUrl , id, description, published);
				
				
				YoutubeTasksActivity.serviceProgress.setProgress(i);
				displaylistArray.add(displaylist);
				
				try
				{
					//Thread.sleep(2000);
					storeData.saveToFile(displaylistArray);
				}
				catch (JSONException | IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		return null;

	}

	@Override
	protected void onPostExecute(ArrayList<Playlist> result)
	{
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	    PlaylistActivity.start(mContext);
		YoutubeTasksActivity task = (YoutubeTasksActivity)mContext;
		task.finish();
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
