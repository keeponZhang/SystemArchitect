package com.darren.architect_day26.okhttp.interceptor;

import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;

/**
 * Created by hcDarren on 2017/11/19.
 */

public interface Interceptor {
    Response intercept(Chain chain) throws IOException;
    interface Chain {
        Request request();

        Response proceed(Request request) throws IOException;
    }
}
