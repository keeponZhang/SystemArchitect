package com.darren.architect_day33.simple1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.darren.architect_day33.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // OkHttp +RxJava + Retrofit
        RetrofitClient.getServiceApi().userLogin("Darren","940223")
                //上面返回的是OkHttpCall，OkHttpCall的泛型参数和Callback的泛型参数一样，Result<UserInfo>相当于Callback<Result<UserInfo>>，Result是基本返回的格式，这里可以在封装一层，即如果是success，代表UserInfo一定会有数据，否则就回调到error
                //具体实现是通过动态代理
                .enqueue(new HttpCallback<UserInfo>(){
                    @Override
                    public void onSucceed(UserInfo result) {
                        // 成功
                        Toast.makeText(MainActivity.this,"成功"+result.toString(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String code, String msg) {
                        // 失败
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                });
    }
}
