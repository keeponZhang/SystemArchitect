package com.darren.architect_day26.okhttp.interceptor;

import android.util.Log;

import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * Created by hcDarren on 2017/11/19.
 */

public class RealInterceptorChain implements Interceptor.Chain {
    final List<Interceptor> interceptors;
    final int               mIndex;
    final Request           mRequest;
    private static final String TAG = "RealInterceptorChain";
    //RealInterceptorChain的实例个数要比Interceptor多一个
    public RealInterceptorChain(List<Interceptor> interceptors, int index, Request request){
        Log.e(TAG, "RealInterceptorChain mIndex: "+index );
        this.interceptors = interceptors;
        this.mIndex = index;
        this.mRequest = request;
    }
    @Override
    public Request request() {
        return mRequest;
    }

    //最后一个Interceptor不会调用chain.proceed(request)，其他必须调用
    //chain.proceed(request)的调用次数与Interceptor个数一样，第一次在RealCall中发起调用
    //第一个次chain.proceed(request)，调用的是第一个interceptor.intercept,其他类似
    @Override
    public Response proceed(Request request) throws IOException {


//         List<Interceptor> interceptors = new ArrayList<>();
//         interceptors.add(new BridgeInterceptor());
//         interceptors.add(new CacheInterceptor());
//         interceptors.add(new CallServerInterceptor());

        //假如只有三个Interceptor，首先调用的是BridgeInterceptor的intercept
        // 方法，传入创建的拦截器链（包含所有的拦截器和索引（下个拦截器调用chain的process方法时会用到该索引））
        //最终返回的response是CallServerInterceptor返回的reponse经过CacheInterceptor再经过BridgeInterceptor
        // 处理的response

	    Log.e(TAG, "proceed: "+ mIndex + " mRequest.url=="+mRequest.url()+" request.url=="+request.url());
        RealInterceptorChain next = new RealInterceptorChain(interceptors,
                mIndex + 1, request);
        Interceptor interceptor = interceptors.get(mIndex);
        Response response = interceptor.intercept(next);
        return response;
    }
}
