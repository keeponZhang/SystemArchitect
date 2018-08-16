package com.darren.architect_day09.navigationbar;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by hcDarren on 2017/9/23.
 * 可以拿过来直接使用的 导航栏
 */

public class NavigationBar extends AbsNavigationBar{

    protected NavigationBar(Builder builder) {
        super(builder);
    }

    /**
     * 导航栏的Builder
     */
    public static class Builder extends AbsNavigationBar.Builder<NavigationBar.Builder>{

        public Builder(Context context, int layoutId, ViewGroup parent) {
            super(context, layoutId, parent);
        }

        @Override
        public NavigationBar create() {
            return new NavigationBar(this);
        }
    }
}
