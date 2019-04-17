package com.darren.architect_day26.okhttp.interceptor;

import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;

/**
 * Created by hcDarren on 2017/11/19.
 */

public class CacheInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 本地有没有缓存，如果有没过期
        /*if(true){
            return new Response(new );
        }*/
        Response proceed = chain.proceed(request);
        return proceed ;
    }
}
