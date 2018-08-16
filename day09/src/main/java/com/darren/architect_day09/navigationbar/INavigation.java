package com.darren.architect_day09.navigationbar;

import android.view.View;
import android.view.ViewGroup;

/**
 * 导航栏的规范
 * Created by hcDarren on 2017/9/23.
 */
public interface INavigation {
    void createNavigationBar();

    /**
     * 绑定参数
     */
    void attachNavigationParams();

    /**
     * 将 NavigationView添加到父布局
     */
    void attachParent(View navigationBar, ViewGroup parent);
}
