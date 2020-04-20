package com.darren.architect_day26.okhttp.interceptor;

import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;

/**
 * Created by hcDarren on 2017/11/19.
 */

public interface Interceptor {
    //拦截器，可以通过chain 对象得到request和reponse
    //这里为什么返回Response，因为需要的最终是Response
    Response intercept(Chain chain) throws IOException;
    interface Chain {
        Request request();

        Response proceed(Request request) throws IOException;
    }
}
