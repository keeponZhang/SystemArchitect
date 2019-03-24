package com.darren.architect_day18.simple3.bottomtab.iterator;

import com.darren.architect_day18.simple3.bottomtab.BottomTabItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcDarren on 2017/10/22.
 */

public class TabListIterator<T extends BottomTabItem> implements TabIterator{
    private List<T> mTabItems;
    private int index;

    public TabListIterator(){
        mTabItems = new ArrayList<>();
    }

    public void addItem(T item){
        mTabItems.add(item);
    }

    @Override
    public BottomTabItem next() {
        return mTabItems.get(index++);
    }

    @Override
    public boolean hashNext() {
        return index < mTabItems.size();
    }
}
