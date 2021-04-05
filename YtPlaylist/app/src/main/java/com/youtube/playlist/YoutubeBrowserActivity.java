package com.youtube.playlist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.Vibrator;
import android.text.TextUtils;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.youtube.playlist.R;
import com.youtube.playlist.utils.AdvancedWebView;
import com.youtube.playlist.utils.BrowserUtils;
import com.youtube.playlist.api.YoutubeApi;
import com.youtube.playlist.analytics.YoutubeAnalytics;
import com.youtube.playlist.utils.YoutubeUtils;
import com.androweb.engine.graphics.typeface.CalligraphyContextWrapper;

public class YoutubeBrowserActivity extends AppCompatActivity implements AdvancedWebView.Listener
{

	private Toolbar mToolbar;
	private LinearLayout mLayout;
    private AdvancedWebView webView;
    private ProgressBar progressBar;
    private FloatingActionButton fabSearch;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout webContainer;
    private FrameLayout progressBarContainer;

    private static final String YOUTUBE_URL = "http://www.youtube.com/";
	private static final String YOUTUBE_WATCH_URL = "http://www.youtube.com/watch?v";
    private static final String YOUTUBE_UPLOAD_URL = "http://www.youtube.com/apk-upload/";

    Integer shortAnimDuration;

    Integer previsionThemeColor = Color.parseColor("#FF8B14");

    SharedPreferences sharedPreferences;

    public static String TAG = YoutubeBrowserActivity.class.getSimpleName();
	public static String EXTRA_URL = "url";
	public static String urlPage;

   	public static void start(final Context c, final String url)
	{
		Intent mApplication = new Intent(c, YoutubeBrowserActivity.class);
		mApplication.putExtra(EXTRA_URL, url);
		c.startActivity(mApplication);
	} 

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_browser);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

	    mLayout = (LinearLayout)findViewById(R.id.main_content);
		//Views
		refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
		progressBar = (ProgressBar)findViewById(R.id.main_progress_bar);
    	//settingsLayoutFragment = (RelativeLayout)view.findViewById(R.id.settings_layout_fragment);
		webContainer = (RelativeLayout)findViewById(R.id.web_container);
		//firstLoadingView = (LinearLayout)view.findViewById(R.id.first_loading_view);
		webView = (AdvancedWebView)findViewById(R.id.main_webview);
		fabSearch = (FloatingActionButton)findViewById(R.id.fab_search);
		//fabSearch.hide();

		progressBarContainer = (FrameLayout)findViewById(R.id.main_progress_bar_container);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(YoutubeBrowserActivity.this);

		shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

		//boolean saveUrl = sharedPreferences.getBoolean("save_url", false);

		webView.setListener(this, this);
		webView.setGeolocationEnabled(false);
		webView.setMixedContentAllowed(true);
		webView.setCookiesEnabled(true);
		webView.setThirdPartyCookiesEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(final WebView view, final String url)
				{
					setFabListener(url);
				}

			});
		webView.setWebChromeClient(chromeClient);
		urlPage = getIntent().getStringExtra(EXTRA_URL); 
		// if no url is passed, close the activity
        if (TextUtils.isEmpty(urlPage))
		{
            finish();
        } 	

		webView.addHttpHeader("X-Requested-With", "");
		webView.loadUrl(urlPage);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh()
				{
					webView.reload();
				}
			});
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_youtube_browser, menu);

        if (BrowserUtils.isBookmarked(this, webView.getUrl()))
		{
            // change icon color
            BrowserUtils.tintMenuIcon(getApplicationContext(), menu.getItem(0), R.color.colorAccent);
        }
		else
		{
            BrowserUtils.tintMenuIcon(getApplicationContext(), menu.getItem(0), android.R.color.black);
        }
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
	{

        // menu item 0-index is bookmark icon

        // enable - disable the toolbar navigation icons
        if (!webView.canGoBack())
		{
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).getIcon().setAlpha(130);
        }
		else
		{
            menu.getItem(1).setEnabled(true);
            menu.getItem(1).getIcon().setAlpha(255);
        }

        if (!webView.canGoForward())
		{
            menu.getItem(2).setEnabled(false);
            menu.getItem(2).getIcon().setAlpha(130);
        }
		else
		{
            menu.getItem(2).setEnabled(true);
            menu.getItem(2).getIcon().setAlpha(255);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
	{

        if (item.getItemId() == android.R.id.home)
		{
            finish();
        }

        if (item.getItemId() == R.id.action_bookmark)
		{
            // bookmark / unbookmark the url
            BrowserUtils.bookmarkUrl(this, webView.getUrl());

            String msg = BrowserUtils.isBookmarked(this, webView.getUrl()) ?
				webView.getTitle() + "is Bookmarked!" :
				webView.getTitle() + " removed!";
            Snackbar snackbar = Snackbar.make(mLayout, msg, Snackbar.LENGTH_LONG);
            snackbar.show();

            // refresh the toolbar icons, so that bookmark icon color changes
            // depending on bookmark status
            invalidateOptionsMenu();
        }

        if (item.getItemId() == R.id.action_back)
		{
            back();
        }

        if (item.getItemId() == R.id.action_forward)
		{
            forward();
        }

        return super.onOptionsItemSelected(item);
    }

    // backward the browser navigation
    private void back()
	{
        if (webView.canGoBack())
		{
            webView.goBack();
        }
    }

    // forward the browser navigation
    private void forward()
	{
        if (webView.canGoForward())
		{
            webView.goForward();
        }
    }
	@SuppressLint("NewApi")
	@Override
	public void onResume()
	{
		super.onResume();
		webView.onResume();
		// ...
	}

	@SuppressLint("NewApi")
	@Override
	public void onPause()
	{
		webView.onPause();
		// ...
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		webView.onDestroy();
		// ...
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		webView.onActivityResult(requestCode, resultCode, intent);
		// ...
	}

	@Override
	public void onPageStarted(String url, Bitmap favicon)
	{
		// TODO: Implement this method
		webView.setVisibility(View.INVISIBLE);
		setFabListener(url);
		//Showing progress bar
		progressBarContainer.animate()
			.alpha(1f)
			.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation)
				{
					super.onAnimationStart(animation);
					progressBarContainer.setVisibility(View.VISIBLE);
				}
			});

	}

	@Override
	public void onPageFinished(String url)
	{
		// TODO: Implement this method
		webView.setVisibility(View.VISIBLE);
		setFabListener(url);
		progressBarContainer.animate()
			.alpha(0f)
			.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation)
				{
					super.onAnimationEnd(animation);		
					progressBarContainer.setVisibility(View.GONE);
				}
			});

        if (refreshLayout.isRefreshing())
		{
            refreshLayout.setRefreshing(false);
        }

    }

	@Override
	public void onPageError(int errorCode, String description, String failingUrl)
	{
		// TODO: Implement this method
		fabSearch.hide();	
        if (errorCode == -2)
		{
			new AlertDialog.Builder(YoutubeBrowserActivity.this)
				.setTitle(R.string.yt_error)
				.setMessage(getString(R.string.yt_error_while_loading_page) + " " + failingUrl + "(" + String.valueOf(errorCode) + " " + description + ")")
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
					{
						YoutubeBrowserActivity.this.finish();
					}
				}).setPositiveButton(R.string.yt_refresh, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
					{
						webView.reload();
					}
				}).show();
        }
	}

	@Override
	public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent)
	{
		// TODO: Implement this method
		if (isWritePermissionGranted())
		{
            download(url, suggestedFilename);
			YoutubeAnalytics.setBrowserHandleDownload(url, suggestedFilename);
        }
		else
		{
			new AlertDialog.Builder(YoutubeBrowserActivity.this)
				.setTitle(R.string.yt_write_permission)
				.setMessage(getString(R.string.yt_storage_access))
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
					{
						YoutubeBrowserActivity.this.finish();
					}
				}).setPositiveButton(R.string.yt_request_permission, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
					{
						webView.reload();
					}
				}).show();
        }
	}

	@Override
	public void onExternalPageRequest(String url)
	{
		// TODO: Implement this method
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

	private void setFabListener(final String url) 
	{
		if (url != null && (url.contains("://youtu.be/") || url.contains("youtube.com/watch?v=")))
		{

			// We have a valid link
			fabSearch.show();	
			fabSearch.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						new AlertDialog.Builder(YoutubeBrowserActivity.this)
							.setNegativeButton("Preview", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface  dialog, int which)
								{
									new YoutubeUtils(YoutubeBrowserActivity.this).addVideoId(url);
									dialog.dismiss();
								}
							})
							.setPositiveButton("Download", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface  dialog, int which)
								{

									YoutubeUtils.addDownloadVideo(YoutubeBrowserActivity.this, url);
									YoutubeAnalytics.setBrowserUrl(url);
									dialog.dismiss();
								}
							})
							.show();
					}
				});
		}
		else if (url != null && (url.contains("youtube.com/channel/")))
		{
			new AlertDialog.Builder(this)
				.setMessage("Anda Ingin Mendownload Video Dari Channel Ini?")
				.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						final String videoID;

						if (url.contains("youtube.com/channel/"))
						{
							String[] videoURLSplit = url.split("/");
							videoID = videoURLSplit[videoURLSplit.length - 1];
						}
						else
						{
							Uri videouri = Uri.parse(url);
							videoID = videouri.getLastPathSegment();
    					}

						new Thread(new Runnable() {
								@Override
								public void run()
								{ 
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append("https://www.y2mate.com/channel/");
									stringBuilder.append(videoID);
									Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
									startActivity(intent);
									YoutubeAnalytics.setBrowserHandleDownload(stringBuilder.toString(), "");

								}
							}).start();
						dialog.dismiss();
					}
				})
				.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						dialog.dismiss();
					}
				}).show();
		}
		else
		{
			fabSearch.hide();	
		}
	} 

    private WebChromeClient chromeClient = new WebChromeClient() {

		@Override
		public void onReceivedTitle(WebView view, String title)
		{
			super.onReceivedTitle(view, title);
			Toast.makeText(YoutubeBrowserActivity.this, title, Toast.LENGTH_SHORT).show();
		}

        @Override
        public void onProgressChanged(WebView view, int progress)
		{

            //update the progressbar value
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animation.setDuration(100); // 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

        }
    };

	private void download(String url, String name)
	{

        if (!sharedPreferences.getBoolean("external_download", false))
		{
            if (AdvancedWebView.handleDownload(YoutubeBrowserActivity.this, url, name))
			{
                Toast.makeText(YoutubeBrowserActivity.this, getString(R.string.yt_download_started), Toast.LENGTH_SHORT).show();
            }
			else
			{
                Toast.makeText(YoutubeBrowserActivity.this, getString(R.string.yt_cant_download), Toast.LENGTH_SHORT).show();
            }
        }
		else
		{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }


    }

    private boolean isWritePermissionGranted()
	{
        return Build.VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(YoutubeBrowserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

	private void crossFade(final View toHide, View toShow)
	{

        toShow.setAlpha(0f);
        toShow.setVisibility(View.VISIBLE);

        toShow.animate()
			.alpha(1f)
			.setDuration(shortAnimDuration)
			.setListener(null);

        toHide.animate()
			.alpha(0f)
			.setDuration(shortAnimDuration)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation)
				{
					toHide.setVisibility(View.GONE);
				}
			});
    }
 	@Override
	protected void attachBaseContext(Context newBase)
	{
		// TODO: Implement this method
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	} 

	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		if (webView.canGoBack())
		{
            webView.goBack();
        }
		else
		{
		    super.onBackPressed();
		}
	}

}
