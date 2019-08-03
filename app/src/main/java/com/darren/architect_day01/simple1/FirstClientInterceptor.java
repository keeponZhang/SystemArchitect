package com.darren.architect_day01.simple1;

import android.util.Log;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * @创建者 keepon
 * @创建时间 2019/3/11 0011 上午 9:54
 * @描述 ${TODO}
 * @版本 $$Rev$$
 * @更新者 $$Author$$
 * @更新时间 $$Date$$
 */
public class FirstClientInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.e("TAG", "FirstClientInterceptor intercept request:");
        try {
            if (!NetworkUtils.isOnline()) {//没网强制从缓存读取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            } else {
                request = request.newBuilder().removeHeader("If-None-Match").build();
            }
        } catch (Exception e) {
            Log.e(TAG, "intercept: Exception");
        }

        Response response = chain.proceed(request);
        Log.e("TAG", "FirstClientInterceptor intercept response:");
        return response;
    }
}
