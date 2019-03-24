package com.darren.architect_day01.simple1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.darren.architect_day01.BaseApplication;
import com.darren.architect_day01.ConstantValue;
import com.darren.architect_day01.R;
import com.darren.architect_day01.Utils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
     int   cacheSize = 10 * 1024 * 1024;
    //分别对应缓存的目录，以及缓存的大小。
    Cache mCache = new Cache(BaseApplication.mApplicationContext.getExternalCacheDir(), cacheSize);
    //在构造 OkHttpClient 时，通过 .cache 配置。
    OkHttpClient mOkHttpClient = new OkHttpClient.Builder().cache(mCache).
            addInterceptor(new FirstClientInterceptor())
            .addNetworkInterceptor(new LastInternetInterceptor()).build();
//    OkHttpClient mOkHttpClient = new OkHttpClient();
    String testUrl = "";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv);
    }

    public void getAppXixiUpdate(View view) {
        testUrl = "http://eapi.ciwong.com/repos/launcher/android/update";
        Log.e("Post请求路径：", testUrl);  //.addHeader("Cache-Control","max-age=5")
        Request.Builder requestBuilder = new Request.Builder().url(testUrl).tag(this);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 失败
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resultJson = response.body().string();
                Log.e("TAG", resultJson);
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(new Date().getTime()+" :  code == "+response.code()+"  "+resultJson);
                    }
                });
                // 1.JSON解析转换
                // 2.显示列表数据
                // 3.缓存数据
            }
        });
    }

    public void getAppMarketUpdate(View view) {
//        testUrl = "http://eapi.ciwong.com/repos/xiappstore/android/update";
//        testUrl = "http://pms.mb.qq.com/rsp204";
        testUrl = "http://www.jianshu.com/";
        Log.e("Post请求路径：", testUrl);
        Request.Builder requestBuilder = new Request.Builder().url(testUrl).tag(this);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 失败
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resultJson = response.body().string();
                Log.e("TAG", resultJson);
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
	                    mTextView.setText(new Date().getTime()+" :  code == "+response.code()+"  "+resultJson);
                    }
                });
                // 1.JSON解析转换
                // 2.显示列表数据
                // 3.缓存数据
            }
        });
    }


    static Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            try {
                if (!NetworkUtils.isOnline()) {//没网强制从缓存读取
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                } else {
                    request = request.newBuilder().removeHeader("If-None-Match").build();
                }
            }catch (Exception e){
                Log.e(TAG, "intercept: Exception");
            }


            Response response = chain.proceed(request);
            Response responseLatest;




                if (NetworkUtils.isOnline()) {
                    int maxAge =  60*5;
                    Log.e(TAG, "intercept: maxAge  "+NetworkUtils.isOnline());
                    responseLatest = setCacheTime(response, maxAge);
                } else {
                    int maxStale = 60 * 60 * 6; // 没网失效6小时
                    Log.e(TAG, "intercept: maxStale "+NetworkUtils.isOnline());
                    responseLatest = setNoNetWorkCacheTime(response, maxStale);
                }


            return responseLatest;


        }
    };

    private static Response setCacheTime(Response response, int maxAge) {
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();
    }

    private static Response setNoNetWorkCacheTime(Response response, int maxStale) {
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                .build();
    }
}
