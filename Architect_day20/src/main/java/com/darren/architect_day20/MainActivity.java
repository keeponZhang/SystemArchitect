package com.darren.architect_day20;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Message message = new Message();
        Message message = Message.obtain();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new Message());


    }

    // 下周六走源码，自己动手写核心部分
}
