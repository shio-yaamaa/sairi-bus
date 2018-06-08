package com.hatenadiary.yaamaa.sairibus;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class PeriodViewGroupMaker {

    static MainActivity mainActivity;
    static Utility utility;

    public static void initialize(MainActivity mainActivity) {
        PeriodViewGroupMaker.mainActivity = mainActivity;
        PeriodViewGroupMaker.utility = new Utility(mainActivity);
    }

    public static View make(String[] names, PeriodView.PeriodType periodType, int[][] times, int[] colors) {

        LinearLayout periodViewGroup = new LinearLayout(mainActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, (int) utility.dpToPx(Constants.MARGIN_BETWEEN_ROWS + Constants.ROW_MAIN_TITLE_HEIGHT), 0, 0);
        periodViewGroup.setLayoutParams(layoutParams);
        periodViewGroup.setOrientation(LinearLayout.VERTICAL);

        // create PeriodViews
        for (int i = 0; i < names.length; i++) {
            FrameLayout frameLayout = new FrameLayout(mainActivity);
            periodViewGroup.addView(frameLayout, LinearLayout.LayoutParams.MATCH_PARENT, (int) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT));

            PeriodView periodView = new PeriodView(mainActivity);
            periodView.initialize(frameLayout, names[i], periodType, times[i], colors[i]);
        }

        return periodViewGroup;
    }
}