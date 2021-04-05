package com.youtube.playlist.utils;

import com.youtube.playlist.R;
import com.youtube.playlist.utils.AdvancedWebView;
import com.youtube.playlist.api.YoutubeApi;

import android.support.annotation.NonNull;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ArrayList; 

import com.youtube.playlist.*;
import com.youtube.playlist.player.YoutubePlayerActivity;
import com.youtube.playlist.tasks.YoutubeTasksActivity;

public class YoutubeUtils
{

    private Context context;

    public YoutubeUtils(Context context)
	{
        this.context = context;
    }

    
	@SuppressLint("DefaultLocale")
    public static String formatTime(float sec)
	{
        int minutes = (int) (sec / 60);
        int seconds = (int) (sec % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public static boolean isOnline(Context context)
	{
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isConnected(Context context)
	{
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
		{
            return true;
        }
		else
		{
            return false;
        }
    }

    public static String convertStreamToString(java.io.InputStream is)
	{
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
	
	public void addVideoId(final String videourl)
	{
        final String thumbnailURL;
        final String videoID;

        if (videourl.contains("youtube.com/watch?v="))
		{
            String[] videoURLSplit = videourl.split("=");
            String[] videoURLSplit_playlist_or_extension = videoURLSplit[1].split("&");
            videoID = videoURLSplit_playlist_or_extension[0];
            thumbnailURL = "https://i.ytimg.com/vi/" + videoURLSplit_playlist_or_extension[0] + "/maxresdefault.jpg";
        }
		else
		{
            Uri videouri = Uri.parse(videourl);
            videoID = videouri.getLastPathSegment();
            thumbnailURL = "https://i.ytimg.com/vi/" + videouri.getLastPathSegment() + "/maxresdefault.jpg";
        }

        new Thread(new Runnable() {
				@Override
				public void run()
				{ 
					Intent Preview = new Intent(context, YoutubePlayerActivity.class);
					Preview.putExtra(YoutubePlayerActivity.TAG_URL, videoID);
					Preview.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(Preview);
				}
			}).start();
    } 

	public static void addDownloadVideo(final Context mContext, final String videourl)
	{
        final String thumbnailURL;
        final String videoID;

        if (videourl.contains("youtube.com/watch?v="))
		{
            String[] videoURLSplit = videourl.split("=");
            String[] videoURLSplit_playlist_or_extension = videoURLSplit[1].split("&");
            videoID = videoURLSplit_playlist_or_extension[0];
            thumbnailURL = "https://i.ytimg.com/vi/" + videoURLSplit_playlist_or_extension[0] + "/maxresdefault.jpg";
        }
		else
		{
            Uri videouri = Uri.parse(videourl);
            videoID = videouri.getLastPathSegment();
            thumbnailURL = "https://i.ytimg.com/vi/" + videouri.getLastPathSegment() + "/maxresdefault.jpg";
        }

        new Thread(new Runnable() {
				@Override
				public void run()
				{
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YoutubeApi.Y2MATE_PAGE_URL + videoID));
					mContext.startActivity(browserIntent);
					//webView.loadUrl(YoutubeApi.Y2MATE_PAGE_URL + videoID);
				}
			}).start();
    }

	public static void addDownloadAudio(final Context mContext, final String videourl)
	{
        final String thumbnailURL;
        final String videoID;

        if (videourl.contains("youtube.com/watch?v="))
		{
            String[] videoURLSplit = videourl.split("=");
            String[] videoURLSplit_playlist_or_extension = videoURLSplit[1].split("&");
            videoID = videoURLSplit_playlist_or_extension[0];
            thumbnailURL = "https://i.ytimg.com/vi/" + videoURLSplit_playlist_or_extension[0] + "/maxresdefault.jpg";
        }
		else
		{
            Uri videouri = Uri.parse(videourl);
            videoID = videouri.getLastPathSegment();
            thumbnailURL = "https://i.ytimg.com/vi/" + videouri.getLastPathSegment() + "/maxresdefault.jpg";
        }

        new Thread(new Runnable() {
				@Override
				public void run()
				{
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YoutubeApi.Y2MATE_PAGE_URL + videoID));
					mContext.startActivity(browserIntent);
					//webView.loadUrl(YoutubeApi.Y2MATE_PAGE_URL + videoID);
				}
			}).start();
    }
	
	public static boolean isSameDomain(String url, String url1)
	{
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url)
	{
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        int dummy = domainKeys[0].equals("www") ? 1 : 0;
        if (length - dummy == 2)
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        else
		{
            if (domainKeys[length - 1].length() == 2)
			{
                return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
			else
			{
                return domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
        }
    }

    public static void tintMenuIcon(Context context, MenuItem item, int color)
	{
        Drawable drawable = item.getIcon();
        if (drawable != null)
		{
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void bookmarkUrl(Context context, String url)
	{
        SharedPreferences pref = context.getSharedPreferences("androidhive", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        // if url is already bookmarked, unbookmark it
        if (pref.getBoolean(url, false))
		{
            editor.putBoolean(url, false);
        }
		else
		{
            editor.putBoolean(url, true);
        }

        editor.commit();
    }

    public static boolean isBookmarked(Context context, String url)
	{
        SharedPreferences pref = context.getSharedPreferences("androidhive", 0);
        return pref.getBoolean(url, false);
    }

	private static boolean underHoneyComb()
	{
		return false;
	}

	private static boolean isLollipop()
	{
		return Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1;
	}

	private static int getDialogTheme()
	{
		return isLollipop() ? R.style.CustomLollipopDialogStyle : 0;
	}

	@SuppressLint("NewApi")
	static AlertDialog.Builder getDialogBuilder(Context context)
	{
		if (underHoneyComb())
		{
			return new AlertDialog.Builder(context);
		}
		else
		{
			return new AlertDialog.Builder(context, getDialogTheme());
		}
	}

	public void search(final AdvancedWebView webView)
	{
		final EditText edit = new EditText(context);
		new AlertDialog.Builder(context)
			.setTitle(R.string.yt_search)
			.setView(edit)
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
				{
					dialog.dismiss();
				}
			}).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
				{
					if (edit != null)
					{
						webView.loadUrl("http://www.youtube.com/results?search_query=" + edit.getText().toString());
					}
					else
					{
						Toast.makeText(context, context.getString(R.string.yt_search_error), Toast.LENGTH_SHORT).show();
					}
				}
			}).show();

    }

	
class Track
{
	String path;
	String title;
	String artist;
	String album;
	long albumId;
	int duration;
    }
}
