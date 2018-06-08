package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

public class CurrentTimeView extends View {

    Utility utility;
    Paint paint;

    int height;
    float current_x;
    float circle_y;

    // constructors
    public CurrentTimeView(Context context) {
        this(context, null);
    }
    public CurrentTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CurrentTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // initialize
        utility = new Utility(context);

        circle_y = (int) utility.dpToPx(Constants.MARGIN_HEIGHT_FOR_CURRENT_TIME - Constants.CURRENT_TIME_CIRCLE_RADIUS);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Constants.CURRENT_TIME_COLOR);
        paint.setTextSize((float) utility.spToPx(7));

        RedrawTimer.addView(this);
    }

    public void adjustSize() {
        onSizeChanged(
                (int) utility.dpToPx(Constants.CONTENT_WIDTH),
                FrameLayout.LayoutParams.MATCH_PARENT,
                getWidth(),
                getHeight()
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.getLayoutParams().width = w;
        this.getLayoutParams().height = h;

        scrollToCurrentTime();

        this.height = getMeasuredHeight();
    }

    /*
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0) {
            this.getLayoutParams().width = (int) utility.dpToPx(Constants.CONTENT_WIDTH);
        } else {
            scrollToCurrentTime();
        }
        this.height = h;
    }*/

    public void scrollToCurrentTime() {
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        current_x = (float) utility.dpToPx(
                Constants.HORIZONTAL_MARGIN + Utility.widthBetweenTimes(Constants.MIN_TIME, Utility.getCurrentTime())
        );
        ((HorizontalScrollView) ((MainActivity) getContext()).findViewById(R.id.horizontal_scroll))
                .smoothScrollTo((int) current_x - rect.width() / 2, 0);
    }

    @Override
    public void onDraw(Canvas canvas) {
        current_x = (float) utility.dpToPx(
                Constants.HORIZONTAL_MARGIN + Utility.widthBetweenTimes(Constants.MIN_TIME, Utility.getCurrentTime())
        );
        canvas.drawCircle(
                current_x,
                circle_y,
                (float) utility.dpToPx(Constants.CURRENT_TIME_CIRCLE_RADIUS),
                paint
        );
        canvas.drawRect(
                (float) (current_x - utility.dpToPx(Constants.CURRENT_TIME_LINE_THICKNESS) / 2),
                circle_y,
                (float) (current_x + utility.dpToPx(Constants.CURRENT_TIME_LINE_THICKNESS) / 2),
                height,
                paint
        );

        String text = String.valueOf(Utility.getCurrentTime() % 60);
        canvas.drawText(
                text,
                current_x - paint.measureText(text) / 2,
                paint.getTextSize(),
                paint
        );
    }
}
