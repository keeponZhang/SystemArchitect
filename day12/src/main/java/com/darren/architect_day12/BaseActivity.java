package com.darren.architect_day12;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hcDarren on 2017/10/1.
 */

public abstract class BaseActivity extends AppCompatActivity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 设置布局
        setContentView();
        // 写一些公用的方法，ButterKnife注解，统一管理Activity，等等
        // 2. 初始化Title
        initTitle();
        // 3. 初始化View
        initView();
        // 4. 访问接口数据（initData）
        initData(savedInstanceState);
    }

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void initView();

    protected abstract void initTitle();

    protected abstract void setContentView();

    // 写一大堆方法 ，很多 Activity 都要用的
    public void startActivity(Class<? extends BaseActivity> clazz) {
        Intent intent =  new Intent(this,clazz);
        startActivity(intent);
    }
}
