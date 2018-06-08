package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class PeriodView extends View {

    Utility utility;

    FrameLayout parent;

    int width;

    String title;
    PeriodType periodType;
    int[] times;

    Paint paint;

    // constructors
    public PeriodView(Context context) {
        super(context);
    }
    public PeriodView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PeriodView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(FrameLayout parent, final String title, PeriodType periodType, int[] times, int color) {

        this.utility = new Utility(getContext());

        this.parent = parent;
        this.title = title;
        this.periodType = periodType;
        this.times = times;

        if (times[0] == -1) {
            return;
        }

        // color
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setTextSize((int) utility.dpToPx(Constants.PERIOD_VIEW_LABEL_FONT_SIZE));

        // set layout
        parent.addView(this);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        layoutParams.width = (int) utility.dpToPx(Utility.widthBetweenTimes(times[0], times[1]));
        layoutParams.height = (int) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT);
        layoutParams.setMargins(
                (int) utility.dpToPx(Constants.HORIZONTAL_MARGIN
                        + Utility.widthBetweenTimes(Constants.MIN_TIME, times[0])),
                0,
                0,
                0
        );

        RedrawTimer.addView(this);
        this.setOnTouchListener(new OnTouchListenerToFocus());

        // show detailed information when clicked
        final ArrayList<String> periodNameList = new ArrayList<>(Arrays.asList(periodType.periodNames));
        final ArrayList<Integer> periodTimeList = new ArrayList<>();
        for (int time : times) {
            periodTimeList.add(time);
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailDialog dialog = new DetailDialog();

                // setup Bundle
                Bundle args = new Bundle();
                args.putString("title", title);
                args.putStringArrayList("name_list", periodNameList);
                args.putIntegerArrayList("time_list", periodTimeList);
                dialog.setArguments(args);

                dialog.show(((MainActivity) getContext()).getSupportFragmentManager(), "dialog");
                RedrawTimer.addView(dialog);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
    }

    public void onDraw(Canvas canvas) {

        if (times[0] == -1) {
            return;
        }

        paint.setAlpha(Utility.isFuture(times[1]) ? 255 : (int) (255 * Constants.PAST_ALPHA));

        // draw label
        if (periodType == PeriodType.CLASSWORK) {
            canvas.drawText(
                    title,
                    (width - paint.measureText(title)) / 2,
                    (float) utility.dpToPx(Constants.PERIOD_VIEW_VERTICAL_MARGIN) + paint.getTextSize(),
                    paint
            );
        }

        // draw line
        float[] yPositions = new float[2];
        if (periodType == PeriodType.CLASSWORK) {
            yPositions[0] = (float) utility.dpToPx(Constants.PERIOD_VIEW_VERTICAL_MARGIN) * 2 + paint.getTextSize();
            yPositions[1] = (float) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT - Constants.PERIOD_VIEW_VERTICAL_MARGIN);
        } else {
            yPositions[0] = (float) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT - Constants.PERIOD_VIEW_FILL_HEIGHT) / 2;
            yPositions[1] = (float) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT + Constants.PERIOD_VIEW_FILL_HEIGHT) / 2;
        }
        canvas.drawRect(0, yPositions[0], width, yPositions[1], paint);
    }

    public enum PeriodType {
        CLASSWORK(new String[]{"開始", "終了"}),
        LIBRARY(new String[]{"開館", "閉館"}),
        RESTAURANT(new String[]{"開店", "閉店"});

        final String[] periodNames;

        PeriodType(String[] periodNames) {
            this.periodNames = periodNames;
        }
    }
}