package com.darren.architect_day26.okhttp;

/**
 * Created by hcDarren on 2017/11/18.
 * Call
 */

public interface Call {
    //异步
    void enqueue(Callback callback);

    //同步
    Response execute();
}
