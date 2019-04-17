package com.darren.architect_day34.retrofit;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

/**
 * Created by hcDarren on 2017/12/17.
 */

public class Retrofit {
    final String baseUrl;
    final okhttp3.Call.Factory callFactory;
    private Map<Method,ServiceMethod> serviceMethodMapCache = new ConcurrentHashMap<>();

    public Retrofit(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
    }

    public <T> T create(Class<T> service) {
        // 检验，是不是一个接口 ，不能让他继承子接口

        // 重点
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 每执行一个方法都会来这里
                // 判断是不是 Object 的方法 ？
                if(method.getDeclaringClass() == Object.class){
                    return method.invoke(this,args);
                }

                // 解析参数注解
                ServiceMethod serviceMethod = loadServiceMethod(method);
                // 2. 封装 retrofit 的OkHttpCall，此时还不是ServiceApi对应方法返回值的OkHttpCall(调了对应的方法后才是)
                OkHttpCall okHttpCall = new OkHttpCall(serviceMethod,args);

                return okHttpCall;
            }
        });
    }

    private ServiceMethod loadServiceMethod(Method method) {
        Log.e("TAG", "loadServiceMethod: "+method );
        // 享元设计模式
        ServiceMethod serviceMethod = serviceMethodMapCache.get(method);
        if(serviceMethod == null){
            serviceMethod = new ServiceMethod.Builder(this,method).build();
            serviceMethodMapCache.put(method,serviceMethod);
        }
        return serviceMethod;
    }

    public static class Builder{
        String baseUrl;
        okhttp3.Call.Factory callFactory;
        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder client(okhttp3.Call.Factory callFactory){
            this.callFactory = callFactory;
            return this;
        }

        public Retrofit build() {
            if(callFactory == null){
                callFactory = new OkHttpClient();
            }
            return new Retrofit(this);
        }
    }
}
