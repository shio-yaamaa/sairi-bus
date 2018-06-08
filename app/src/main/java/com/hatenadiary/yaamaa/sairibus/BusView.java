package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class BusView extends View {

    Utility utility;

    FrameLayout parent;
    Bus bus;
    int color;

    int width;
    int height;

    Path path;

    Path leftMicrobePath;
    Path rightMicrobePath;
    Matrix leftMicrobeMatrix;
    Matrix rightMicrobeMatrix;
    RectF microbeRectF;

    Paint paint;

    // constructors
    public BusView(Context context) {
        super(context);
    }
    public BusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public BusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // initializer
    public void initialize(FrameLayout parent, final Bus bus, int color) {
        utility = new Utility(getContext());

        this.parent = parent;
        this.bus = bus;
        this.color = color;

        path = new Path();

        leftMicrobePath = new Path();
        rightMicrobePath = new Path();
        microbeRectF = new RectF(
                (float) (utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS - Constants.BUS_VIEW_MICROBE_THICKNESS / 2)),
                0,
                (float) (utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS + Constants.BUS_VIEW_MICROBE_THICKNESS / 2)),
                (float) utility.dpToPx(Constants.BUS_VIEW_HEIGHT / 2)
        );
        leftMicrobeMatrix = new Matrix();
        rightMicrobeMatrix = new Matrix();
        leftMicrobeMatrix.postRotate(-45, microbeRectF.centerX(), microbeRectF.centerY());
        rightMicrobeMatrix.postRotate(+45, microbeRectF.centerX(), microbeRectF.centerY());
        leftMicrobePath.addRect(microbeRectF, Path.Direction.CW);
        rightMicrobePath.addRect(microbeRectF, Path.Direction.CW);
        leftMicrobePath.transform(leftMicrobeMatrix);
        rightMicrobePath.transform(rightMicrobeMatrix);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        // set layout
        parent.addView(this);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        layoutParams.width = (int) utility.dpToPx(
                Utility.widthBetweenTimes(bus.times[0], bus.times[bus.times.length - 1])
                        + Constants.BUS_VIEW_CIRCLE_RADIUS * 2
        );
        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(
                (int) utility.dpToPx(Constants.HORIZONTAL_MARGIN
                        + Utility.widthBetweenTimes(Constants.MIN_TIME, bus.times[0])
                        - Constants.BUS_VIEW_CIRCLE_RADIUS),
                0,
                0,
                0
        );

        RedrawTimer.addView(this);
        this.setOnTouchListener(new OnTouchListenerToFocus());

        // show detailed information when clicked
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailDialog dialog = new DetailDialog();

                // setup Bundle
                Bundle args = new Bundle();
                args.putString("title", "便名: "+bus.name);

                ArrayList<String> nameList
                        = (ArrayList<String>) BusManager.directionList.get(bus.section).get(bus.direction).clone();
                ArrayList<Integer> timeList = new ArrayList<>();
                int deleteCount = 0;
                for (int i = 0; i < bus.times.length; i++) {
                    if (bus.times[i] == -1) {
                        nameList.remove(i - deleteCount);
                        deleteCount++;
                    } else {
                        timeList.add(bus.times[i]);
                    }
                }
                args.putStringArrayList("name_list", nameList);
                args.putIntegerArrayList("time_list", timeList);
                args.putBoolean("two_buses", bus.twoBuses);
                args.putBoolean("micro_disease", bus.microDisease);
                args.putBoolean("bus_holiday", HolidayManager.isHoliday);
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
        this.height = h;
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (HolidayManager.isHoliday) {
            paint.setAlpha((int) (255 * Constants.BUS_HOLIDAY_ALPHA));
        } else if (Utility.isFuture(bus.times[bus.times.length - 1])) {
            paint.setAlpha(255);
        } else {
            paint.setAlpha((int) (255 * Constants.PAST_ALPHA));
        }

        path.reset();

        // draw circles
        for (int time : bus.times) {
            if (time != -1) {
                path.addCircle(
                        (float) utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS + Utility.widthBetweenTimes(bus.times[0], time)),
                        height / 2,
                        (float) utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS),
                        Path.Direction.CW
                );
            }
        }

        if (bus.twoBuses) {
            // draw two lines
            path.addRect(
                    (float) utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS),
                    (float) (height / 2 - utility.dpToPx(Constants.BUS_VIEW_MARGIN_BETWEEN_LINES / 2 + Constants.BUS_VIEW_LINE_THICKNESS)),
                    (float) (width - utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS)),
                    (float) (height - utility.dpToPx(Constants.BUS_VIEW_MARGIN_BETWEEN_LINES)) / 2,
                    Path.Direction.CW
            );
            path.addRect(
                    (float) utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS),
                    (float) (height + utility.dpToPx(Constants.BUS_VIEW_MARGIN_BETWEEN_LINES)) / 2,
                    (float) (width - utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS)),
                    (float) (height / 2 + utility.dpToPx(Constants.BUS_VIEW_MARGIN_BETWEEN_LINES / 2 + Constants.BUS_VIEW_LINE_THICKNESS)),
                    Path.Direction.CW
            );
        } else {
            // draw a line
            path.addRect(
                    (float) utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS),
                    (float) (height - utility.dpToPx(Constants.BUS_VIEW_LINE_THICKNESS)) / 2,
                    (float) (width - utility.dpToPx(Constants.BUS_VIEW_CIRCLE_RADIUS)),
                    (float) (height + utility.dpToPx(Constants.BUS_VIEW_LINE_THICKNESS)) / 2,
                    Path.Direction.CW
            );
        }

        if (bus.microDisease) {
            // draw microbe
            path.addPath(leftMicrobePath, 0, (float) utility.dpToPx(Constants.BUS_VIEW_MICROBE_VERTICAL_OFFSET));
            path.addPath(rightMicrobePath, 0, (float) utility.dpToPx(Constants.BUS_VIEW_MICROBE_VERTICAL_OFFSET));
        }

        canvas.drawPath(path, paint);
    }

}