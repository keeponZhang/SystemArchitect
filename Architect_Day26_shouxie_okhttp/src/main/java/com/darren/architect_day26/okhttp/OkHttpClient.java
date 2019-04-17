package com.darren.architect_day26.okhttp;

/**
 * Created by hcDarren on 2017/11/18.
 * OkHttpClient
 */
// OkHttpClient不是真正发起网络请求的地方
// 一般设置一些链接超时
// 设置https 证书的一些参数
// 设置拦截器
// 等等
public class OkHttpClient {
    final Dispatcher dispatcher;
    private OkHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
    }

    public OkHttpClient() {
        this(new Builder());
    }

    //OkHttpClient会new出一个RealCall对象，然后执行call的异步或者同步方法
    public Call newCall(Request request) {
        return RealCall.newCall(request,this);
    }

    public static class Builder{
        Dispatcher dispatcher;
        // 链接超时
        // https 证书的一些参数
        // 拦截器
        // 等等
        public Builder(){
            dispatcher = new Dispatcher();
        }

        public OkHttpClient build(){
            return new OkHttpClient(this);
        }
    }
}
