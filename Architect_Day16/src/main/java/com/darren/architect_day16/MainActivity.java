package com.darren.architect_day16;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day16.simple3.DarrenRetrofit;
import com.darren.architect_day16.simple3.ServiceInterface;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DarrenRetrofit retrofit = new DarrenRetrofit();
        // 核心代码 ServiceInterface.class 接口的 Class 会返回一个 ServiceInterface 的实例对象
        ServiceInterface serviceInterface = retrofit.create(ServiceInterface.class);
        // 能看懂
        // String result = serviceInterface.userLogin();
        // Log.e("TAG","返回值 = "+result);

        serviceInterface.userRegister();
    }
}
