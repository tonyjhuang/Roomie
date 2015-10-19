package com.roomie.roomie.ui.partials;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

/**
 * Created by tonyjhuang on 10/15/15.
 */
public class AutoCompleteTextViewBackListener extends AutoCompleteTextView {

    private ImeBackListener mOnImeBack;

    public AutoCompleteTextViewBackListener(Context context) {
        super(context);
    }

    public AutoCompleteTextViewBackListener(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteTextViewBackListener(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack();
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(ImeBackListener listener) {
        mOnImeBack = listener;
    }

    public interface ImeBackListener {
        void onImeBack();
    }

}
