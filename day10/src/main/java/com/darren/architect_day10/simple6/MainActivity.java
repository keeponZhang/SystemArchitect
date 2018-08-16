package com.darren.architect_day10.simple6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.darren.architect_day10.R;
import com.darren.architect_day10.simple5.IOHandler;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.test_tv);
        // 问题的分析 ？ 我们一般都会有一个清理缓存的功能的 ，是不是以后需要去清理，
        // 清理的时候某些特定内容我们可能不想清理，可能以后为了保证性能，我们采取磁盘的存储内存的存储或者采取数据库的存储等等
        IOHandler ioHandler =  IOHandlerFactory.getInstance().getDefaultIOHandler();
        ioHandler.save("userName","darren");
        ioHandler.save("userAge","940223");

    }

    public void click(View view){
        IOHandler ioHandler = IOHandlerFactory.getInstance().getDefaultIOHandler();
        String userName = ioHandler.getString("userName");
        String userAge = ioHandler.getString("userAge");
        mTextView.setText("userName = "+userName+" userAge = "+userAge);
    }
}
