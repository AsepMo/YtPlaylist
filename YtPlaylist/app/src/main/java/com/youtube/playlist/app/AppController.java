package com.youtube.playlist.app;

import android.app.Application;
import android.content.Context;

import com.youtube.playlist.R;
import com.androweb.engine.graphics.typeface.CalligraphyConfig;
import com.androweb.engine.widget.TextField;
import com.androweb.engine.widget.CustomViewWithTypefaceSupport;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                      .setDefaultFontPath("fonts/Roboto-ThinItalic.ttf")
                                      .setFontAttrId(R.attr.fontPath)
                                      .addCustomViewWithSetTypeface(CustomViewWithTypefaceSupport.class)
                                      .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                                      .build()); 
		
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    
    public static Context getContext()
    {
        return mInstance.getApplicationContext();
	}

}
