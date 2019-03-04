package com.iqianbang.fanpai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by wenguangjun on 2017/8/24.
 */

public class myScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener;

    public interface ScrollViewListener {

        void onScrollChanged(int x, int y, int oldx, int oldy);

    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public myScrollView(Context context) {
        super(context);
    }

    public myScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(l, t, oldl, oldt);
        }
    }
}
