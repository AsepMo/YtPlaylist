package com.youtube.playlist.utils;

import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsSession;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsCallback;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog; 
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List; 

import com.youtube.playlist.R;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.PlaylistActivity;

public class BrowserUtils {
 
	public static String APP_PREF_NAME = "beranda";
	public static void compileVideoId(final String videourl)
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
				}
			}).start();
    } 
	
	public static void compileVideoId(final AdvancedWebView webView, final String videourl)
	{
       final String videoID;
        if (videourl.contains("youtube.com/watch?v="))
		{
            String[] videoURLSplit = videourl.split("=");
            String[] videoURLSplit_playlist_or_extension = videoURLSplit[1].split("&");
            videoID = videoURLSplit_playlist_or_extension[0];
        }
		else
		{
            Uri videouri = Uri.parse(videourl);
            videoID = videouri.getLastPathSegment();
        }

        new Thread(new Runnable() {
				@Override
				public void run()
				{
					webView.loadUrl(YoutubeApi.Y2MATE_PAGE_URL + videoID);
				}
			}).start();
    } 
	
	public static boolean isSameDomain(String url, String url1) {
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url) {
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        int dummy = domainKeys[0].equals("www") ? 1 : 0;
        if (length - dummy == 2)
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        else {
            if (domainKeys[length - 1].length() == 2) {
                return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
            } else {
                return domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
        }
    }

    public static void tintMenuIcon(Context context, MenuItem item, int color) {
        Drawable drawable = item.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void bookmarkUrl(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        // if url is already bookmarked, unbookmark it
        if (pref.getBoolean(url, false)) {
            editor.putBoolean(url, false);
        } else {
            editor.putBoolean(url, true);
        }

        editor.commit();
    }

    public static boolean isBookmarked(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(url, false);
    } 
	
	public static String getBookmark(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("beranda", c.getString(R.string.beranda));
    }

	private static boolean underHoneyComb() {
		return false;
	}

	private static boolean isLollipop() {
		return Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1;
	}

	private static int getDialogTheme() {
		return isLollipop() ? R.style.CustomLollipopDialogStyle : 0;
	}

	@SuppressLint("NewApi")
	public static AlertDialog.Builder getDialogBuilder(Context context) {
		if (underHoneyComb()) {
			return new AlertDialog.Builder(context);
		} else {
			return new AlertDialog.Builder(context, getDialogTheme());
		}
	}

	public static void setLink(Activity mContext , String Url)
	{
		CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
		intentBuilder.setToolbarColor(R.color.colorPrimary);

		//Generally you do not want to decode bitmaps in the UI thread.
		String shareLabel = mContext.getString(R.string.label_action_share);
		Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),android.R.drawable.ic_menu_share);
		PendingIntent pendingIntent = createPendingIntent(mContext);
		intentBuilder.setActionButton(icon, shareLabel, pendingIntent);


		String menuItemTitle = mContext.getString(R.string.menu_item_title);
		PendingIntent menuItemPendingIntent = zrockGithub(mContext);
		intentBuilder.addMenuItem(menuItemTitle, menuItemPendingIntent);

		intentBuilder.setShowTitle(true);
		intentBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.abc_ic_ab_back_material));
		intentBuilder.setStartAnimations(mContext, R.anim.sv_slide_in, R.anim.sv_slide_out);
		intentBuilder.setExitAnimations(mContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

		CustomTabActivityHelper.openCustomTab(mContext, intentBuilder.build(), Uri.parse(Url), new WebviewFallback());
    }

    private static PendingIntent createPendingIntent(Activity mContext)
	{
        Intent actionIntent = new Intent(mContext, ShareBroadcastReceiver.class);
        return PendingIntent.getBroadcast(mContext, 0, actionIntent, 0);
    }

	private static final String url = "https://github.com/AWeb41/";
	private static PendingIntent zrockGithub(Activity mContext)
	{
        Intent actionIntent = new Intent(mContext, PlaylistActivity.class);
		//actionIntent.putExtra("url", url);
        return PendingIntent.getActivity(mContext, 0, actionIntent, 0);
    }
}
