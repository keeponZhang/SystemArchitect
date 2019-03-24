package com.darren.architect_day24;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient okHttpClient = new OkHttpClient();
        // 307    Location:https://www.baidu.com
        //  1. 构建一个请求 ，url,端口，请求头的一些参数，表单提交（contentType,contentLength）
        Request request = new Request.Builder()
                .url("http://www.baidu.com").build();
        //  2. 把 Request 封装转成一个 RealCall
        Call call = okHttpClient.newCall(request);
        // 3. enqueue 队列处理 执行
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            // 1-3 中小型企业
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("TAG",result);
            }
        });
    }
}
