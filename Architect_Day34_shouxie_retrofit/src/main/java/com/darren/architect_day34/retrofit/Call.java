package com.darren.architect_day34.retrofit;

/**
 * Created by hcDarren on 2017/12/17.
 */

public interface Call<T> {
    void enqueue(Callback<T> callback);
}
