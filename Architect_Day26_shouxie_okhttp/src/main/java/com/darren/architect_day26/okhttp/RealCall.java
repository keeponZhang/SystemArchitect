package com.darren.architect_day26.okhttp;

import android.util.Log;

import com.darren.architect_day26.okhttp.interceptor.BridgeInterceptor;
import com.darren.architect_day26.okhttp.interceptor.CacheInterceptor;
import com.darren.architect_day26.okhttp.interceptor.CallServerInterceptor;
import com.darren.architect_day26.okhttp.interceptor.Interceptor;
import com.darren.architect_day26.okhttp.interceptor.RealInterceptorChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcDarren on 2017/11/18.
 */

public class RealCall implements Call{
    //持有request
    private final Request orignalRequest;
    //持有OkHttpClient
    private final OkHttpClient client;
    public RealCall(Request request, OkHttpClient client) {
        orignalRequest = request;
        this.client = client;
    }

    public static Call newCall(Request request, OkHttpClient client) {
        return new RealCall(request,client);
    }

    //异步方法
    @Override
    public void enqueue(Callback callback) {
        // 异步的，这里创建了一个AsyncCall，实际是一个runnable对象
        AsyncCall asyncCall = new AsyncCall(callback);
        // 交给线程池，真正的网络请求在线程池执行，逻辑在AsyncCall的run方法，随即又调用了AsyncCall的execute方法
        client.dispatcher.enqueue(asyncCall);
    }

    @Override
    public Response execute() {
        return null;
    }

    final class AsyncCall extends NamedRunnable {
        Callback callback;
        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected void execute() {
            // 来这里，开始访问网络 Request -> Response
            Log.e("TAG","execute");
            // Volley xUtils Afinal AsyHttpClient
            // 基于 HttpUrlConnection , OkHttp = Socket + okio(IO)
            final Request request = orignalRequest;
            try {
                //这里写的比较简单,真正okhttp会有interceptors和networkInterceptors，如注释
                List<Interceptor> interceptors = new ArrayList<>();
//                interceptors.addAll(client.interceptors());// interceptors
                interceptors.add(new BridgeInterceptor());
                interceptors.add(new CacheInterceptor());
//                interceptors.addAll(this.client.networkInterceptors());
                interceptors.add(new CallServerInterceptor());
                //最开始的RealInterceptorChain拥有一开始的interceptors和orignalRequest
                //RealInterceptorChain的核心是拥有所有的interceptors和当前要执行的interceptor索引
                Interceptor.Chain chain = new RealInterceptorChain
                        (interceptors,0,orignalRequest);
                Response response = chain.proceed(request);
                callback.onResponse(RealCall.this,response);
            } catch (IOException e) {
                callback.onFailure(RealCall.this,e);
            }
        }
    }
}
