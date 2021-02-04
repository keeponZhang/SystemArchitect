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

        //chain的procee方法，会创建另一个链，
        // 会传所有的拦截器和重要的index（可以区分要拿到具体拿到哪个拦截器),然后会拿当前index的的拦截器，
        // 调用拦截器的intercept方法，传入刚创建的chain
        Response proceed(Request request) throws IOException;
    }
}
