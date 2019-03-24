package com.darren.architect_day33;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day33.simple1.RetrofitClient;
import com.darren.architect_day33.simple1.UserInfo;
import com.darren.architect_day33.simple2.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OkHttp +RxJava + Retrofit
        RetrofitClient.getServiceApi().userLogin("Darren1","940223")
                .enqueue(new Callback<Result<UserInfo>>() {
                    @Override
                    public void onResponse(Call<Result<UserInfo>> call, Response<Result<UserInfo>> response) {
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
