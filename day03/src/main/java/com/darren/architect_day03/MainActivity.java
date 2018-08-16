package com.darren.architect_day03;

import android.os.Bundle;

public class MainActivity extends BaseSkinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // HttpUtils   10个类   20类

        HttpUtils.with().post.execute();

        ButterKnife.inject(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void changeSkin() {

    }
}
