package com.darren.architect_day14.simple4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darren.architect_day14.R;

import java.util.List;

/**
 * Created by hcDarren on 2017/10/8.
 * 具体的适配器 - 相当于simple3的Adapter
 * 把数据集合适配成View - 对象适配
 */

public class ListAdapter implements AdapterTarget {
    private List<String> mItems;
    private Context context;
    public ListAdapter(List<String> items, Context context){
        this.mItems = items;
        this.context = context;
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, ViewGroup parent) {
        TextView itemView = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.item_main,parent,false);
        itemView.setText(mItems.get(position));
        return itemView;
    }
}
