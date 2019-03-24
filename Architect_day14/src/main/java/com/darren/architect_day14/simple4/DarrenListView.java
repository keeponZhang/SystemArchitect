package com.darren.architect_day14.simple4;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by hcDarren on 2017/10/8.
 * 简单的ListView 不考虑复用
 */

public class DarrenListView extends ScrollView{
    private LinearLayout mContainer;
    private AdapterTarget mAdapter;

    public DarrenListView(Context context) {
        this(context,null);
    }

    public DarrenListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DarrenListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContainer = new LinearLayout(context);
        mContainer.setOrientation(LinearLayout.VERTICAL);
        addView(mContainer,0);
    }

    @Override
    public void addView(View child) {
        mContainer.addView(child);
    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
        // 观察者，注册反注册
        int count = mAdapter.getCount();
        for (int i=0;i<count;i++){
            View childView = mAdapter.getView(i,mContainer);
            addView(childView);
        }
    }
}
