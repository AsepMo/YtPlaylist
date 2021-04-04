package com.androweb.engine.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;

public class AppsPageUtils {

	public static boolean isApplication;
    public static boolean isInstalled(Context context, String packageName) {
        boolean exist = false;
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.activityInfo.packageName.equals(packageName)) {
                exist = true;
            }
        }
        return exist;
    }
	
	public static void onApplication(Context c, String packName)
	{
		try
		{
			Intent newActivity = c.getPackageManager().getLaunchIntentForPackage(packName);
			newActivity.setAction(Intent.ACTION_VIEW);
			newActivity.addCategory(Intent.CATEGORY_LAUNCHER);
			c.startActivity(newActivity);
			isApplication = true;
		}
		catch (Exception e)
		{
			e.getMessage();
			isApplication = false;
			Toast.makeText(c, "Not App Installed", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void onBrowser(Context c, String url)
	{
		try
		{
			Intent newActivity = new Intent();
			newActivity.setAction(Intent.ACTION_VIEW);
			newActivity.setData(Uri.parse(url));
			newActivity.addCategory(Intent.CATEGORY_BROWSABLE);
			c.startActivity(newActivity);
			isApplication = true;
		}
		catch (Exception e)
		{
			e.getMessage();
			isApplication = false;
			Toast.makeText(c, "Not App Installed", Toast.LENGTH_SHORT).show();
		}
	}
}
