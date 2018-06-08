package com.hatenadiary.yaamaa.sairibus;

import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchListenerToFocus implements View.OnTouchListener {

    static ColorDrawable backgroundDrawable = new ColorDrawable();

    public static void initialize() {
        backgroundDrawable.setColor(Constants.FOCUS_COLOR);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                v.setBackground(backgroundDrawable);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                v.setBackground(null);
                break;
            }
        }
        return false;
    }
}
