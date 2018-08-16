package com.darren.architect_day09.navigationbar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darren.architect_day09.R;

/**
 * Created by hcDarren on 2017/9/23.
 * 可以拿过来直接使用的 默认样式导航栏
 */

public class DefaultNavigationBar extends AbsNavigationBar<DefaultNavigationBar.Builder>{

    protected DefaultNavigationBar(Builder builder) {
        super(builder);
    }

    @Override
    public void attachNavigationParams() {
        super.attachNavigationParams();
        // 处理特有的
        TextView leftView = findViewById(R.id.back_tv);
        leftView.setVisibility(getBuilder().mLeftVisible);
    }

    /**
     * 导航栏的Builder
     */
    public static class Builder extends AbsNavigationBar.Builder<DefaultNavigationBar.Builder>{
        public int mLeftVisible = View.VISIBLE;

        public Builder(Context context, ViewGroup parent) {
            super(context, R.layout.ui_defualt_navigation_bar, parent);
        }

        @Override
        public DefaultNavigationBar create() {
            return new DefaultNavigationBar(this);
        }

        public Builder setLeftText(String text){
            setText(R.id.back_tv,text);
            return this;
        }

        public Builder setLeftClickListener(View.OnClickListener clickListener){
            setOnClickListener(R.id.back_tv,clickListener);
            return this;
        }

        public Builder hideLeftText() {
            mLeftVisible = View.INVISIBLE;
            return this;
        }
    }
}
