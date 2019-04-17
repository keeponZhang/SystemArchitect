package com.darren.architect_day26.okhttp.interceptor;

import android.util.Log;

import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.RequestBody;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;

/**
 * Created by hcDarren on 2017/11/19.
 */

public class BridgeInterceptor implements Interceptor{

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("TAG","BridgeInterceptor");
        //所有Interceptor的request都是同一个对象
        Request request = chain.request();
//        request.setUrl("http://www.baidu.com");
        // 添加一些请求头
        request.header("Connection","keep-alive");
        // 做一些其他处理
        if(request.requestBody()!=null){
            RequestBody requestBody = request.requestBody();
            request.header("Content-Type",requestBody.getContentType());
            request.header("Content-Length",Long.toString(requestBody.getContentLength()));
        }
        //这里会调用下一个Interceptor的intercept方法
        Response response = chain.proceed(request);

        return response;
    }
}
