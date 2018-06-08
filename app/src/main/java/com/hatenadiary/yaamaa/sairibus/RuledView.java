package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class RuledView extends View {

    Utility utility;

    Paint textPaint;
    Paint mainPaint;
    Paint subPaint;

    int height;

    // constructors
    public RuledView(Context context) {
        this(context, null);
    }
    public RuledView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public RuledView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // initialize
        utility = new Utility(context);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Constants.TIME_TEXT_COLOR);
        textPaint.setTextSize((int) utility.spToPx(Constants.TIME_FONT_SIZE));

        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(Constants.MAIN_RULE_COLOR);

        subPaint = new Paint();
        subPaint.setAntiAlias(true);
        subPaint.setColor(Constants.SUB_RULE_COLOR);
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

        this.height = getMeasuredHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {

        // draw lines
        for (int main = 0; main < (Constants.MAX_TIME - Constants.MIN_TIME) / 60 + 1; main++) {

            // main rule
            float mainX = (float) utility.dpToPx(
                    Constants.HORIZONTAL_MARGIN + 60 * Constants.WIDTH_PER_MINUTE * main
            );
            canvas.drawLine(mainX, (float) utility.dpToPx(Constants.TIME_HEIGHT), mainX, height, mainPaint);

            // time text
            String text = String.valueOf(main + Constants.MIN_TIME / 60);
            float textWidth = textPaint.measureText(text);
            canvas.drawText(
                    text,
                    mainX - textWidth / 2,
                    (float) utility.dpToPx(Constants.TIME_HEIGHT - 5),
                    textPaint
            );

            // sub rule
            for (int sub = 0; sub < Constants.RULE_COUNT_PER_HOUR; sub++) {
                float subX = mainX + (float) utility.dpToPx(
                        60 * Constants.WIDTH_PER_MINUTE / (Constants.RULE_COUNT_PER_HOUR + 1) * (sub + 1)
                );
                canvas.drawLine(subX, (float) utility.dpToPx(Constants.TIME_HEIGHT), subX, height, subPaint);
            }

        }
    }
}
