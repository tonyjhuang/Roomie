package com.roomie.roomie.ui.partials;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.roomie.roomie.R;
import com.roomie.roomie.ui.utils.TypefaceManager;

/**
 * Created by tonyjhuang on 10/18/15.
 */
public class RoomieTextView extends TextView {
    private String base;

    public RoomieTextView(Context context) {
        super(context);
        setTypeface(base);
    }

    public RoomieTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.RoomieTextView,
                0, 0);
        try {
            base = a.getString(R.styleable.RoomieTextView_typeface) + ".ttf";
        } finally {
            a.recycle();
        }

        setTypeface(base);
    }

    public void setTypeface(String typeface) {
        setTypeface(TypefaceManager.get(getContext(), typeface));
    }

}
