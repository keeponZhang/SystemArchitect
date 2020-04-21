package com.darren.architect_day01.simple1;

import android.util.Log;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * createBy keepon
 */
public class MyCacheInterceptor extends LastInternetInterceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.w("TAG", this.getClass().getSimpleName() + " intercept request:");
        Response response = chain.proceed(request);
        Response responseLatest = response;

        //这些是在response后处理
        if (!NetworkUtils.isOnline()) {
            //这里只有在interceptor中有用，netInterceptor不会走到这里
            int maxStale = 20; // 没网失效6小时
            Log.e(TAG,
                    this.getClass().getSimpleName() + "intercept: 放宽过期时间 maxStale " + maxStale +
                            "    " +
                            "-!!!!!!!!!!!!!!!!!!!!!!!");
            responseLatest = setNoNetWorkCacheTime(response, maxStale);
        }
        Log.w("TAG", this.getClass().getSimpleName() + " intercept Response:");

        return responseLatest;
    }
}
