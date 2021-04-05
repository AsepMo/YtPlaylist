package com.youtube.playlist.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.UUID;

import com.youtube.playlist.data.YoutubeData;
import com.youtube.playlist.R;
import com.youtube.playlist.Playlist;
import com.youtube.playlist.PlaylistStarter;
import com.youtube.playlist.PlaylistActivity;


public class YoutubeDeleteNotification extends IntentService {

    private YoutubeData storeRetrieveData;
    private ArrayList<Playlist> mToDoItems;
    private Playlist mItem;

    public YoutubeDeleteNotification() {
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        storeRetrieveData = new YoutubeData(this, YoutubeData.FILENAME);
        UUID todoID = (UUID) intent.getSerializableExtra(YoutubeNotification.TODOUUID);

        mToDoItems = loadData();
        if (mToDoItems != null) {
            for (Playlist item : mToDoItems) {
                if (item.getIdentifier().equals(todoID)) {
                    mItem = item;
                    break;
                }
            }

            if (mItem != null) {
                mToDoItems.remove(mItem);
                dataChanged();
                saveData();
            }

        }

    }

    private void dataChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(PlaylistStarter.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PlaylistStarter.CHANGE_OCCURED, true);
        editor.apply();
    }

    private void saveData() {
        try {
            storeRetrieveData.saveToFile(mToDoItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private ArrayList<Playlist> loadData() {
        try {
            return storeRetrieveData.loadFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
