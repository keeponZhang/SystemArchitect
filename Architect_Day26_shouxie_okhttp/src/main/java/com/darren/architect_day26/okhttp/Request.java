package com.darren.architect_day26.okhttp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hcDarren on 2017/11/18.
 * 客户端的请求
 */
public class Request {
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;
    public final Method             method;
    final        Map<String,String> headers;
    final        RequestBody        requestBody;
    public Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.requestBody = builder.requestBody;
    }

    public RequestBody requestBody() {
        return requestBody;
    }

    public String url() {
        return url;
    }

    public void header(String key, String value) {
        headers.put(key,value);
    }

    public static class Builder {
        // String url , Body post参数 ，请求头
        String url;
        Method method;
        Map<String,String> headers;
        RequestBody requestBody;
        public Builder(){
            method = Method.GET;
            headers = new HashMap<>();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Request build() {
            return new Request(this);
        }

        public Builder get(){
            method = Method.GET;
            return this;
        }

        public Builder post(RequestBody body){
            method = Method.POST;
            this.requestBody = body;
            return this;
        }

        public void header(String key,String value){
            headers.put(key,value);
        }
    }
}
