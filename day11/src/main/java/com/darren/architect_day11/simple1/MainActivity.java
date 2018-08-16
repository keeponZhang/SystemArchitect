package com.darren.architect_day11.simple1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day11.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 一般的写法new对象调用方法
        TeacherEat teacherEat = new TeacherEat();
        teacherEat.eat();
        // 装饰设计模式怎么写，一般情况都是把类对象作为构造参数传递

    }
}
