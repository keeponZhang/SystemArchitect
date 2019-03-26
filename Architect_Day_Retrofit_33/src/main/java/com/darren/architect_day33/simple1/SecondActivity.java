package com.darren.architect_day33.simple1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day33.R;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OkHttp +RxJava + Retrofit  //无封装时
        RetrofitClient.getServiceApi().userLogin("Darren1","940223")
                .enqueue(new Callback<Result<UserInfo>>() {
                    @Override  //retrofit原来的回调是onResponse
                    public void onResponse(Call<Result<UserInfo>> call, Response<Result<UserInfo>> response) {
                        //此时这个泛型是result.data的类型，不方便，这里自己还有用gson转
                        Result<UserInfo> result = response.body();
                        if(result.isOk()){
                            //

                        }
                    }

                    @Override
                    public void onFailure(Call<Result<UserInfo>> call, Throwable t) {
                    }
                });
    }
}
