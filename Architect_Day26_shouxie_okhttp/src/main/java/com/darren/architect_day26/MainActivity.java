package com.darren.architect_day26;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.darren.architect_day26.okhttp.Call;
import com.darren.architect_day26.okhttp.Callback;
import com.darren.architect_day26.okhttp.OkHttpClient;
import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.RequestBody;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void getFrom() {
        RequestBody requestBody = new RequestBody()
                .type(RequestBody.FORM)
                .addParam("pageNo", 1+"")
                .addParam("platform", "android");
        final Request request = new Request.Builder()
                .url("https://api.saiwuquan.com/api/appv2/sceneModel")
                .post(requestBody)
                .build();
        final Request request2 = new Request.Builder()
                //没处理302，如果是http://www.jianshu.com，没正确结果返回
                .url("https://www.jianshu.com")
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.newCall(request2).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //e.printStackTrace();
                Log.e("TAG", "出错了");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = "";
                if(response!=null){
                    result = response.string();
                }
                Log.e("TAG", result);
            }
        });
    }

    public void getFrom(View view) {
        getFrom();
    }
}
