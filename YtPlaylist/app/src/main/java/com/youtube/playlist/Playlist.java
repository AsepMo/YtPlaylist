package com.youtube.playlist;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.Context;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.List;
import java.lang.reflect.Method;

import com.youtube.playlist.utils.TimeAgo;

public class Playlist implements Serializable { 

    public static final String TAG = "Playlist";


    private Context mContext;
    private Toast mToast;

    private String mTitle;
    private String mThumbnails;
    private String mVideoId;
    private String mDescription;
    private String mPublished;
	private String timeAgo;
	
    private int mYoutubeColor;
    private Date mYoutubeDate;

    private boolean mHasUpdater;

    private UUID mIdentifier;

    //add description
    public static final String TITLE = "title";
    public static final String THUMBNAILS = "thumbnails";
    public static final String DESCRIPTIONS = "description";
    public static final String VIDEOID = "videoId";
    public static final String PUBLISHED = "publishedAt";
    public static final String YOUTUBECOLOR = "color";
    public static final String YOUTUBEUPDATE= "update";

    public static final String IDENTIFIER = "identifier";

    public Playlist(String title, String thumbnail_url, String videoid, String description, String published, boolean update) {
        this.mTitle = title;
        this.mThumbnails = thumbnail_url;
        this.mVideoId = videoid;
        this.mDescription = description;
        this.mHasUpdater = update;
        this.mPublished = published;
        this.mYoutubeColor = 1677725;
        this.mIdentifier = UUID.randomUUID();
    }

    public Playlist(JSONObject jsonObject) throws JSONException {
        mTitle = jsonObject.getString(TITLE);
        mThumbnails = jsonObject.getString(THUMBNAILS);
        mDescription = jsonObject.getString(DESCRIPTIONS);
        mVideoId = jsonObject.getString(VIDEOID);
        mHasUpdater = jsonObject.getBoolean(YOUTUBEUPDATE);
        mYoutubeColor = jsonObject.getInt(YOUTUBECOLOR);
        mPublished = jsonObject.getString(PUBLISHED);
        mIdentifier = UUID.fromString(jsonObject.getString(IDENTIFIER));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TITLE, mTitle);
        jsonObject.put(THUMBNAILS, mThumbnails);
        jsonObject.put(DESCRIPTIONS, mDescription);
        jsonObject.put(VIDEOID, mVideoId);
        jsonObject.put(YOUTUBECOLOR, mYoutubeColor);
        jsonObject.put(YOUTUBEUPDATE, mHasUpdater);
        jsonObject.put(PUBLISHED, mPublished);
        jsonObject.put(IDENTIFIER, mIdentifier.toString()); 
        return jsonObject;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setThumbnail(String mThumbnail) {
        this.mThumbnails = mThumbnail;
    }

    public String getThumbnailUrl() {
        return mThumbnails;
    }

    public void setVideoId(String mVideoId) {
        this.mVideoId = mVideoId;
    }

    public String getVideoID() {
        return mVideoId;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDescription() {
        return mDescription;
    }
	
	public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    
    public void setPublished(String mPublished) {
        try {
            DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
            DateTime dt = parser.parseDateTime(mPublished);

            long videoSecs = (dt.getMillis())/1000;
            long nowSecs = (new Date().getTime())/1000;
            long secs = nowSecs - videoSecs;
            setTimeAgo(TimeAgo.getTimeAgo(secs));
        } catch (Exception e) {
            e.printStackTrace();
            setTimeAgo("NA");
        } finally {
            this.mPublished = mPublished;
        }   
	}

    public String getPublished() {
        return mPublished;
    }

    public boolean getUpdater() {
        return mHasUpdater;
    }

    public void setHasUpdater(boolean mHasReminder) {
        this.mHasUpdater = mHasReminder;
    }

    public void setYoutubeDate(Date mToDoDate) {
        this.mYoutubeDate = mToDoDate;
    }

    public Date getYoutubeDate() {
        return mYoutubeDate;
    }

    public void setYoutubeColor(int mItemsColor) {
        this.mYoutubeColor = mItemsColor;
    }

    public int getYoutubeColor() {
        return mYoutubeColor;
    }

    public UUID getIdentifier() {
        return mIdentifier;
    }

}
