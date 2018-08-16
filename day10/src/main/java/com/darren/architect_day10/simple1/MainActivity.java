package com.darren.architect_day10.simple1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.darren.architect_day10.R;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.test_tv);
        // 用户的登录信息或者其他信息大家怎么保存的  sp 数据库 等等

        SharedPreferences preferences = getSharedPreferences("cache", Context.MODE_PRIVATE);
        preferences.edit().putString("userName","darren").putString("userAge","880223").commit();

        // SharedPreferences 的数据其实是存到哪里去了？ 其实也是操作的文件，但是文件是 xml
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName","darren");
        editor.putString("userAge","880223");
        editor.commit();
    }

    public void click(View view){
        SharedPreferences preferences = getSharedPreferences("cache", Context.MODE_PRIVATE);
        String userName = preferences.getString("userName","");
        String userAge = preferences.getString("userAge","880223");
        mTextView.setText("userName = "+userName+" userAge = "+userAge);
    }
}
