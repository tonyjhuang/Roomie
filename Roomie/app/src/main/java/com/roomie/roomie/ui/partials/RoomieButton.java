package com.roomie.roomie.ui.partials;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

import com.roomie.roomie.R;


/**
 * Created by tonyjhuang on 10/18/15.
 */
public class RoomieButton extends AppCompatButton {
    public RoomieButton(Context context) {
        this(context, null);
    }

    public RoomieButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomieButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RoomieButton, defStyleAttr, 0);

        int color = a.getColor(R.styleable.RoomieButton_buttonColor,
                getResources().getColor(R.color.colorPrimary));

        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{color});
        setSupportBackgroundTintList(csl);

        a.recycle();
    }
}
