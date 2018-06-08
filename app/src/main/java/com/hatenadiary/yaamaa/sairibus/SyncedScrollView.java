package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class SyncedScrollView extends ScrollView {
    static List<SyncedScrollView> scrollViewList = new ArrayList<>();

    public SyncedScrollView(Context context) {
        this(context, null);
    }
    public SyncedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SyncedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // initialize
        scrollViewList.add(this);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (SyncedScrollView scrollView : scrollViewList) {
            if (scrollView != this && (scrollView.getScrollX() != l || scrollView.getScrollY() != t)) {
                scrollView.scrollTo(l, t);
            }
        }
    }
}