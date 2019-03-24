package com.darren.architect_day34;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.darren.architect_day34.retrofit.Call;
import com.darren.architect_day34.retrofit.Callback;
import com.darren.architect_day34.simple.RetrofitClient;
import com.darren.architect_day34.simple.UserLoginResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void getData(View view) {
        RetrofitClient.getServiceApi().userLogin("Darren", "940223")
                .enqueue(new Callback<UserLoginResult>() {
                    @Override
                    public void onResponse(Call<UserLoginResult> call, Response<UserLoginResult> response) {
                        Log.e("TAG","Thread.currentThread()=="+Thread.currentThread());

                        Log.e("TAG",response.body.toString());
                    }

                    @Override
                    public void onFailure(Call<UserLoginResult> call, Throwable t) {

                    }
                });
    }
}
