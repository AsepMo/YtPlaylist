package com.youtube.playlist.downloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DownloadFinishedReceiver extends BroadcastReceiver
{



    @Override
    public void onReceive(final Context context, Intent intent)
	{
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
		{
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            long downloadId = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            q.setFilterById(downloadId);
            Cursor c = ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).query(q);
            if (c.moveToFirst())
			{
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL)
				{
                    String inPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    String dlTitle = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    c.close();

					
				}
			} 
		}

	}
	
}
