package com.hatenadiary.yaamaa.sairibus;

import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RedrawTimer extends Timer {

    static RedrawTimer redrawTimer;

    static MainActivity activity;
    static List<Object> viewList = new ArrayList<>();

    public static void initialize(MainActivity activity) {
        RedrawTimer.activity = activity;
    }

    private RedrawTimer() {
        super();

        final Handler handler = new Handler();
        this.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        redraw();
                    }
                });
            }
        }, Utility.getNextRoundedTime(), Constants.REDRAW_SPAN);
    }

    public static void updateInstance() {
        redrawTimer = new RedrawTimer();
    }

    public static RedrawTimer getInstance() {
        if (redrawTimer == null) {
            redrawTimer = new RedrawTimer();
        }
        return redrawTimer;
    }

    public static void redraw() {
        // if date is not the same as the date in MainActivity, initialize the program
        Calendar calendar = new GregorianCalendar();
        if (!Utility.isSameDate(MainActivity.currentDate, calendar)) {
            activity.recreate();
        }

        for (Object view : viewList) {
            if (view != null) {
                if (view instanceof View) {
                    ((View) view).invalidate();
                } else if (view instanceof DetailDialog) {
                    ((DetailDialog) view).invalidate();
                }
            }
        }
    }

    public static void addView(Object view) {
        if (viewList.indexOf(view) == -1) {
            viewList.add(view);
        }
    }
}