package com.youtube.playlist;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.Toast;

import com.youtube.playlist.adapter.CustomListAdapter;
import com.youtube.playlist.app.AppController;
import com.youtube.playlist.model.Movie;
import com.youtube.playlist.listener.Contract;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.youtube.playlist.model.YoutubeVideoModel;
import com.youtube.playlist.main.VideoPlayerActivity;


public class YoutubeActivity extends AppCompatActivity implements Contract,AdapterView.OnItemClickListener
{

    private static final String TAG = YoutubeActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "https://asepmo-designer.000webhostapp.com/github.json";
    String urlVideo = "https://cldup.com/smVYfhBfim.mp4";
	private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;
	
	@Override
	public void onMoview(Movie movie)
	{
		// TODO: Implement this method
		Toast.makeText(this, movie.getUrl(), Toast.LENGTH_SHORT).show();
		Intent mVideo = new Intent(YoutubeActivity.this, VideoPlayerActivity.class);
		mVideo.putExtra(VideoPlayerActivity.TAG_URL, movie.getUrl());
		startActivity(mVideo);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor("#1b1b1b")));

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
			new Response.Listener<JSONArray>() {
				@Override
				public void onResponse(JSONArray response)
				{
					Log.d(TAG, response.toString());
					hidePDialog();

					// Parsing json
					for (int i = 0; i < response.length(); i++)
					{
						try
						{

							JSONObject obj = response.getJSONObject(i);
							Movie movie = new Movie();
							movie.setTitle(obj.getString("title"));
							movie.setThumbnailUrl(obj.getString("image"));
							movie.setRating(((Number) obj.get("rating")).doubleValue());
							movie.setYear(obj.getInt("releaseYear"));

							// Genre is json array
							JSONArray genreArry = obj.getJSONArray("genre");
							ArrayList<String> genre = new ArrayList<String>();
							for (int j = 0; j < genreArry.length(); j++)
							{
								genre.add((String) genreArry.get(j));
							}
							movie.setGenre(genre);
							movie.setUrl(obj.getString("url"));
							// adding movie to movies array
							movieList.add(movie);

						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}

					}

					// notifying list adapter about data changes
					// so that it renders the list view with updated data
					adapter.notifyDataSetChanged();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error)
				{
					VolleyLog.d(TAG, "Error: " + error.getMessage());
					hidePDialog();

				}
			});

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id)
	{
		// TODO: Implement this method
		Movie mMovie = movieList.get(position); 
		((Contract)YoutubeActivity.this).onMoview(mMovie);
	
	}

    @Override
    public void onDestroy()
	{
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog()
	{
        if (pDialog != null)
		{
            pDialog.dismiss();
            pDialog = null;
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		switch(item.getItemId()){
			case R.id.action_settings:
				Intent mYoutube = new Intent(YoutubeActivity.this, YtPlaylistActivity.class);
				startActivity(mYoutube);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

}
