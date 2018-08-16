package com.darren.architect_day11.simple3;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hcDarren on 2017/9/30.
 */

public class WrapRecyclerView extends RecyclerView{

    private WrapRecyclerAdapter mAdapter;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = new WrapRecyclerAdapter(adapter);
        super.setAdapter(mAdapter);
    }

    /**
     * 添加头部
     * @param view
     */
    public void addHeaderView(View view){
        // 必须要设置 Adapter 之后才能添加头部和底部
        if(mAdapter != null){
            mAdapter.addHeaderView(view);
        }
    }

    /**
     * 添加底部
     * @param view
     */
    public void addFooterView(View view){
        if(mAdapter != null){
            mAdapter.addFooterView(view);
        }
    }

    /**
     * 移除头部
     * @param view
     */
    public void removeHeaderView(View view){
        if(mAdapter != null){
            mAdapter.removeHeaderView(view);
        }
    }

    /**
     * 移除底部
     * @param view
     */
    public void removeFooterView(View view){
        if(mAdapter != null){
            mAdapter.removeFooterView(view);
        }
    }
}
