package com.darren.architect_day34.retrofit;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by hcDarren on 2017/12/17.
 */

public class RequestBuilder {
    ParameterHandler<Object>[] parameterHandlers;
    Object[] args;
    //把请求参数也通过HttpUrl.Builder的addQueryParameter，最终构建HttpUrl
    HttpUrl.Builder httpUrl;
    public RequestBuilder(String baseUrl, String relativeUrl, String httpMethod, ParameterHandler<?>[] parameterHandlers, Object[] args) {
        this.parameterHandlers = (ParameterHandler<Object>[]) parameterHandlers;
        this.args = args;
        httpUrl = HttpUrl.parse(baseUrl+relativeUrl).newBuilder();
    }

    public Request build() {
        int count = args.length;

        for (int i=0;i < count;i++) {
            // userName = Darren
            parameterHandlers[i].apply(this,args[i]);
        }
        // POST 等等（创建okhttp request）
        Request request = new Request.Builder().url(httpUrl.build()).build();
        return request;
    }

    //okhttp url 参数放在httpUrl
    public void addQueryName(String key, String value) {
        // userName = Darren&password = 940223
        httpUrl.addQueryParameter(key,value);
    }
    // 其他
}
