package com.darren.architect_day11.simple3;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hcDarren on 2017/9/30.
 * 装饰设计模式的 RecyclerView.Adapter，我们对其进行功能扩展，使它支持头部和底部的添加
 */
public class WrapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "WrapRecyclerAdapter";
    // 原来的RecyclerView.Adapter，并不支持头部和底部的添加
    private final RecyclerView.Adapter mRealAdapter;
    ArrayList<View> mHeaderViews;
    ArrayList<View> mFooterViews;

    public WrapRecyclerAdapter(RecyclerView.Adapter realAdapter){
        this.mRealAdapter = realAdapter;

        mRealAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Log.e(TAG, "onChanged: " );
//                notifyDataSetChanged();
            }
        });

        mHeaderViews = new ArrayList<>();
        mFooterViews = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        // 问题如果想知道是哪个部分，必须要知道 position 也就是位置 ，但是目前只有 viewType
        // 头部返回 头部的ViewHolder
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return createHeaderFooterViewHolder(mHeaderViews.get(position));
        }
        // mRealAdapter 返回 mRealAdapter的ViewHolder
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mRealAdapter != null) {
            adapterCount = mRealAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                // 直接传 position ,不兼容 万能适配多布局条目
                return mRealAdapter.onCreateViewHolder(parent,mRealAdapter.getItemViewType(adjPosition));
            }
        }
        // 底部返回 底部的ViewHolder
        // Footer (off-limits positions will throw an IndexOutOfBoundsException)
        return createHeaderFooterViewHolder(mFooterViews.get(adjPosition - adapterCount));
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view){};
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 把位置作为 viewType
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 这个方法先不写，测试一下
        // 头部和底部是都不需要做处理的，只要 mRealAdapter 要去做处理
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return ;
        }

        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mRealAdapter != null) {
            adapterCount = mRealAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mRealAdapter.onBindViewHolder(holder,position);
            }
        }
    }

    @Override
    public int getItemCount() { // 总共返回多少条 = 底部条数+头部条数+真实的Adapter条数
        return mFooterViews.size()+mHeaderViews.size()+mRealAdapter.getItemCount();
    }

    /**
     * 添加头部
     * @param view
     */
    public void addHeaderView(View view){
        if(!mHeaderViews.contains(view)){
            mHeaderViews.add(view);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加底部
     * @param view
     */
    public void addFooterView(View view){
        if(!mFooterViews.contains(view)){
            mFooterViews.add(view);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除头部
     * @param view
     */
    public void removeHeaderView(View view){
        if(mHeaderViews.contains(view)){
            mHeaderViews.remove(view);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除底部
     * @param view
     */
    public void removeFooterView(View view){
        if(mFooterViews.contains(view)){
            mFooterViews.remove(view);
            notifyDataSetChanged();
        }
    }
}
