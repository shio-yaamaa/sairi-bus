package com.hatenadiary.yaamaa.sairibus;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RowTitleMaker {

    static MainActivity mainActivity;
    static Utility utility;
    static LayoutInflater layoutInflater;

    public static void initialize(MainActivity mainActivity) {
        RowTitleMaker.mainActivity = mainActivity;
        RowTitleMaker.utility = new Utility(mainActivity);
        RowTitleMaker.layoutInflater = LayoutInflater.from(mainActivity);
    }

    public static View make(CharSequence mainTitle, int mainTitleColor, int edgeColor, int wholeHeight) {
        // inflate row_title
        LinearLayout rowTitle = (LinearLayout) layoutInflater.inflate(R.layout.row_title, null);

        // set LayoutParams
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams((int) utility.dpToPx(Constants.ROW_TITLE_WIDTH), wholeHeight);
        layoutParams.setMargins(0, (int) utility.dpToPx(Constants.MARGIN_BETWEEN_ROWS), 0, 0);
        rowTitle.setLayoutParams(layoutParams);

        // setMainTitle
        TextView titleView = (TextView) rowTitle.findViewById(R.id.row_main_title);
        titleView.setText(mainTitle);
        titleView.setTextColor(mainTitleColor);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) Constants.ROW_MAIN_TITLE_FONT_SIZE);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        titleView.setSingleLine();
        titleView.setGravity(Gravity.CENTER_VERTICAL);

        // set edgeColor
        rowTitle.findViewById(R.id.row_edge).setBackgroundColor(edgeColor);

        return rowTitle;
    }

    public static View make(CharSequence mainTitle, int mainTitleColor, int mainTitleHeight, int edgeColor,
                            CharSequence[] subtitles, final CharSequence[] SnackbarTexts, int[] subtitleHeights, int[] subtitleColors, int subtitleLayoutGravity) {
        // inflate row_title
        ViewGroup rowTitle = (ViewGroup) layoutInflater.inflate(R.layout.row_title, null);

        // set LayoutParams
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams((int) utility.dpToPx(Constants.ROW_TITLE_WIDTH), LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) utility.dpToPx(Constants.MARGIN_BETWEEN_ROWS), 0, 0);
        rowTitle.setLayoutParams(layoutParams);

        // setMainTitle
        final TextView mainTitleView = (TextView) rowTitle.findViewById(R.id.row_main_title);
        mainTitleView.setText(mainTitle);
        mainTitleView.setTextColor(mainTitleColor);
        mainTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) Constants.ROW_MAIN_TITLE_FONT_SIZE);
        mainTitleView.setSingleLine();
        mainTitleView.setHeight(mainTitleHeight);

        // set edgeColor
        rowTitle.findViewById(R.id.row_edge).setBackgroundColor(edgeColor);

        // set subtitles
        ViewGroup rowTitleTextContainer = (ViewGroup) rowTitle.findViewById(R.id.row_title_text_container);
        for (int i = 0; i < subtitles.length; i++) {
            final int finalI = i;

            TextView subtitleView = new TextView(mainActivity);
            subtitleView.setText(subtitles[i]);
            subtitleView.setOnTouchListener(new OnTouchListenerToFocus());
            subtitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(mainActivity.findViewById(android.R.id.content), SnackbarTexts[finalI], Snackbar.LENGTH_LONG).show();
                }
            });
            subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) Constants.ROW_SUBTITLE_FONT_SIZE);
            subtitleView.setSingleLine();
            subtitleView.setEllipsize(TextUtils.TruncateAt.END);
            subtitleView.setHeight(subtitleHeights[i]);
            subtitleView.setGravity(subtitleLayoutGravity);
            subtitleView.setTextColor(subtitleColors[i]);

            rowTitleTextContainer.addView(subtitleView);
        }

        return rowTitle;
    }

}