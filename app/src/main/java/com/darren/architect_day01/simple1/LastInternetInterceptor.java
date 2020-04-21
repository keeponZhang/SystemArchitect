package com.darren.architect_day01.simple1;

import android.util.Log;

import java.io.IOException;

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
//RealCall 可以看出InternetInterceptor是放在CallServerInterceptor之前，就是倒数第二个位置，一般处理返回来的response
public class LastInternetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.w("TAG",  this.getClass().getSimpleName()+" intercept request:");
        Response response = chain.proceed(request);
        Response responseLatest = response;

        //这些是在response后处理
        if (NetworkUtils.isOnline()) {
            int maxAge = 5;
            Log.e("TAG", this.getClass().getSimpleName()+"intercept: maxAge  " + NetworkUtils.isOnline());
            responseLatest = setCacheTime(response, maxAge);
            // responseLatest = response;
        }
        Log.w("TAG", this.getClass().getSimpleName()+" intercept Response:");

        return responseLatest;
    }

    protected  Response setCacheTime(Response response, int maxAge) {
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
//				.header("Cache-Control", "private,max-age=20")
//				.header("Cache-Control", "no-store" )
                .build();
    }

    protected   Response setNoNetWorkCacheTime(Response response, int maxStale) {
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                .build();
    }
}
