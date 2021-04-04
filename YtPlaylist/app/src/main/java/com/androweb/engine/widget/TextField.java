package com.androweb.engine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.youtube.playlist.R;

public class TextField extends TextView {

    public TextField(final Context context, final AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }

}
