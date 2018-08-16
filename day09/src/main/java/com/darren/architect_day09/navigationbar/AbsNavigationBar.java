package com.darren.architect_day09.navigationbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hcDarren on 2017/9/23.
 * 这个是导航栏的基类
 */

public class AbsNavigationBar<B extends AbsNavigationBar.Builder> implements INavigation {
    private B mBuilder;
    private View mNavigationBar;

    protected AbsNavigationBar(B builder) {
        this.mBuilder = builder;
        createNavigationBar();
    }

    @Override
    public void createNavigationBar() {
        mNavigationBar = LayoutInflater.from(mBuilder.mContext)
                .inflate(mBuilder.mLayoutId, mBuilder.mParent, false);
        // 添加
        attachParent(mNavigationBar, mBuilder.mParent);
        // 绑定参数
        attachNavigationParams();
    }

    /**
     * 绑定参数
     */
    @Override
    public void attachNavigationParams() {
        // 设置文本
        Map<Integer,CharSequence> textMaps = mBuilder.mTextMaps;
        for (Map.Entry<Integer, CharSequence> entry : textMaps.entrySet()) {
            TextView textView = findViewById(entry.getKey());
            textView.setText(entry.getValue());
        }
        // 设置点击事件
        Map<Integer,View.OnClickListener> clickListenerMaps = mBuilder.mCLickListenerMaps;
        for (Map.Entry<Integer, View.OnClickListener> entry : clickListenerMaps.entrySet()) {
            View view = findViewById(entry.getKey());
            view.setOnClickListener(entry.getValue());
        }
    }

    public <T extends View> T findViewById(int viewId) {
        return (T) mNavigationBar.findViewById(viewId);
    }

    /**
     * 将 NavigationView添加到父布局
     */
    @Override
    public void attachParent(View navigationBar, ViewGroup parent) {
        parent.addView(navigationBar, 0);
    }

    /**
     * 返回 Builder
     *
     * @return
     */
    public B getBuilder() {
        return mBuilder;
    }

    /**
     * Builder 构建类
     * 构建 NavigationBar 还有存储参数
     */
    public static abstract class Builder<B extends Builder> {
        public Context mContext;
        public int mLayoutId;
        public ViewGroup mParent;
        public Map<Integer, CharSequence> mTextMaps;
        public Map<Integer, View.OnClickListener> mCLickListenerMaps;

        public Builder(Context context, int layoutId, ViewGroup parent) {
            this.mContext = context;
            this.mLayoutId = layoutId;
            this.mParent = parent;
            mTextMaps = new HashMap<>();
            mCLickListenerMaps = new HashMap<>();
        }

        /**
         * 用来创建 NavigationBar
         *
         * @return
         */
        public abstract AbsNavigationBar create();

        // 返回的是 AbsNavigationBar 的 Builder ,但是当我们调用 create() 方法的时候会报错

        /**
         * 设置文本
         *
         * @param viewId
         * @param text
         * @return
         */
        public B setText(int viewId, String text) {
            mTextMaps.put(viewId, text);
            return (B) this;
        }

        /**
         * 设置点击事件
         *
         * @param viewId
         * @param clickListener
         * @return
         */
        public B setOnClickListener(int viewId, View.OnClickListener clickListener) {
            mCLickListenerMaps.put(viewId, clickListener);
            return (B) this;
        }
    }
}
