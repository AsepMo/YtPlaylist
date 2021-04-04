package com.youtube.playlist.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.youtube.playlist.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoPlayerActivity extends Activity {
	private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
	public static final String TAG_URL = "video";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get the layout from video_main.xml
        setContentView(R.layout.activity_video_player);
		String url = getIntent().getStringExtra(TAG_URL);
		
        if (mediaControls == null) {
            mediaControls = new MediaController(VideoPlayerActivity.this);
        }

        if(!isConnect()){
            new AlertDialog.Builder(VideoPlayerActivity.this)
				.setTitle("Tidak Ada Koneksi Internet!")
				.setMessage("Dibutuhkan Akses Koneksi Internet Untuk Mengakses Fitur Ini.\n\nPastikan Perangkat Android Anda Terhubung Dengan Internet.\n\nSilahkan Coba Lagi!")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				}).show();
        }

        // Find your VideoView in your video_main.xml layout
        myVideoView = (VideoView) findViewById(R.id.video);

		
        // Create a progressbar
        progressDialog = new ProgressDialog(VideoPlayerActivity.this);
        // Set progressbar title
        progressDialog.setTitle("Republic of Telesandi Video");
        // Set progressbar message
        progressDialog.setMessage("Tunggu Sebentar...");

        progressDialog.setCancelable(false);
        // Show progressbar
        progressDialog.show();

        try {
            myVideoView.setMediaController(mediaControls);
            //myVideoView.setVideoURI(Uri.parse("https://cldup.com/smVYfhBfim.mp4"));
			myVideoView.setVideoURI(Uri.parse(url));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        myVideoView.setOnPreparedListener(new OnPreparedListener() {
				// Close the progress bar and play the video
				public void onPrepared(MediaPlayer mp) {
					progressDialog.dismiss();
					myVideoView.seekTo(position);
					if (position == 0) {
						myVideoView.start();
					} else {
						myVideoView.pause();
					}
				}
			});

        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer mp){
					finish();
				}
			});
    }

	public boolean isConnect() {
        ConnectivityManager cm =
			(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }
}
