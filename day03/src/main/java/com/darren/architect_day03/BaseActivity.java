package com.darren.architect_day03;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hcDarren on 2017/9/3.
 */

public abstract class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initView();
        
        initData();
    }

    protected abstract void initData();

    protected abstract void initView();
}
