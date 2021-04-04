package com.youtube.playlist.folders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

public class YoutubeFolder
{
    public static final String TAG = "YoutubeFolder";
    private static YoutubeFolder mInstance;
    public static void initFolder(YoutubeFolder engine)
    {
        mInstance = engine;
    }

    public static YoutubeFolder get()
    {
        if (mInstance == null)
        {
            mInstance = new YoutubeFolder(new Builder(getApplicationContext()));
        }
        return mInstance;
    }

    private final String mFolder;
    private final boolean isFolder;
    public static Context getApplicationContext()
    {
        return getApplicationContext();
    }

    public static String EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static String ZFOLDER = EXTERNAL_DIR + "/YtPlaylist";
    public static String ZFOLDER_APK = ZFOLDER + "/1.Apk";
    public static String ZFOLDER_IMG = ZFOLDER + "/2.Image";
    public static String ZFOLDER_AUDIO = ZFOLDER + "/3.Audio";
    public static String ZFOLDER_AUDIO_RECORDER = ZFOLDER_AUDIO + "/Recorder";
    public static String ZFOLDER_AUDIO_DOWNLOAD = ZFOLDER_AUDIO + "/Download";
    public static String ZFOLDER_AUDIO_Convert = ZFOLDER_AUDIO + "/Convert";
    public static String ZFOLDER_VIDEO = ZFOLDER + "/4.Video";
    public static String ZFOLDER_VIDEO_RECORDER = ZFOLDER_VIDEO + "/Recorder";
    public static String ZFOLDER_VIDEO_DOWNLOAD = ZFOLDER_VIDEO + "/Download";
    public static String ZFOLDER_VIDEO_CONVERTED = ZFOLDER_VIDEO + "/Convert";
    public static String ZFOLDER_YOUTUBE = ZFOLDER_VIDEO + "/Youtube";
    public static String ZFOLDER_YOUTUBE_ANALYTICS = ZFOLDER_YOUTUBE + "/Analytics";
    public static String ZFOLDER_YOUTUBE_DOWNLOAD = ZFOLDER_YOUTUBE + "/Download";
    public static String ZFOLDER_EBOOK = ZFOLDER + "/5.Ebook";
    public static String ZFOLDER_SCRIPTME = ZFOLDER + "/6.ScriptMe";
    public static String ZFOLDER_ARCHIVE = ZFOLDER + "/7.Archive";

    public static String setExternalFileDir(Context c, String folder)
    {
        return c.getExternalFilesDir(folder).getParentFile().mkdirs() + "/.nomedia";
    }

    public static String getExternalCacheDir(Context c)
    {
        return c.getExternalCacheDir().getAbsolutePath();
    }

    public static String getInternalCacheDir(Context c)
    {
        return c.getCacheDir().getAbsolutePath();
    }

    public static String getExternalFileDir(Context c, String folder)
    {
        return c.getExternalFilesDir(folder).getAbsolutePath();
    }

    public static String getInternalFileDir(Context c)
    {
        return c.getFilesDir().getAbsolutePath();
    }

    public static String getHomeDir(Context c)
    {
        return c.getFilesDir().getAbsolutePath() + "/home";
    }

    public static String getUserDir(Context c)
    {
        return c.getFilesDir().getAbsolutePath() + "/user";
    }

    protected YoutubeFolder(Builder builder)
    {
        mFolder = builder.mFolder;
        isFolder = builder.isFolder;
    }

    public String getFolder()
    {
        return mFolder;
    }

    public boolean getPath()
    {
        return isFolder;
    }

    public static class Builder
    {
        private boolean isFolder = false;
        private String mFolder = null;
        private Context mContext;

        public Builder(Context context)
        {
            this.mContext = context;
        }

        public Builder setDefaultFolder(String Folder)
        {
            File files = new File(Folder);
            if (!files.exists())
            {
                files.mkdirs();
            }

            File nomedia = new File(files, ".nomedia");
            if (nomedia.exists() && nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException io)
                {
                    io.getMessage();
                }
            }
            setInternalFileDir(mContext);

            String home = YoutubeFolder.getHomeDir(mContext);
            File mHome = new File(home);
            if (!mHome.exists())
            {
                mHome.mkdirs();
            }

            String user = YoutubeFolder.getUserDir(mContext);
            File mUser = new File(user);
            if (!mUser.exists())
            {
                mUser.mkdirs();
            }
            return this;
        }

        public Builder setFolder(String folder)
        {
            this.isFolder = !TextUtils.isEmpty(folder);
            this.mFolder = folder;
            File mFolderMe = new File(folder);
            if (!mFolderMe.exists())
            {
                mFolderMe.mkdirs();
            }
            return this;
        }

        public Builder setFolderApk(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_APK, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderImage(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_IMG, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderScriptMe(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_SCRIPTME, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderAudio(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_AUDIO, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderVideo(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_VIDEO, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderVideoConverted(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_VIDEO_CONVERTED, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderYoutube(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_YOUTUBE, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderYoutube_Analytics(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_YOUTUBE_ANALYTICS, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderYoutube_Download(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_YOUTUBE_DOWNLOAD, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }


        public Builder setFolderEbook(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_EBOOK, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        public Builder setFolderArchive(boolean isFolder)
        {
            File nomedia = new File(ZFOLDER_ARCHIVE, "/.nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists() || !nomedia.isFile())
            {
                try
                {
                    nomedia.createNewFile();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            return this;
        }

        private void setInternalFileDir(Context c)
        {
            getInternalFileDir(c);
        }

        public Builder setInternalCacheDir(Context c)
        {
            getInternalCacheDir(c);
            return this;
        }

        public Builder setExternalFileDir(Context c, String folder)
        {
            c.getExternalFilesDir(folder).getAbsolutePath();
            return this;
        }

        public YoutubeFolder build()
        {
            this.isFolder = !TextUtils.isEmpty(mFolder);
            return new YoutubeFolder(this);
        }
    }
}

