package com.darren.architect_day16;

import com.darren.architect_day16.retrofit2.Call;
import com.darren.architect_day16.retrofit2.http.GET;
import com.darren.architect_day16.retrofit2.http.Headers;
import com.darren.architect_day16.retrofit2.http.Query;

/**
 * description:
 * author: Darren on 2017/10/11 10:56
 * email: 240336124@qq.com
 * version: 1.0
 */
public interface DataServiceInterface{
    @GET("api/appv2/sceneModel")
    @Headers("content:type")
    Call<Result> testMethod(@Query("age") int age);

    @GET("api/appv2/sceneModel")
    @Headers("content:type")
    Call<Result> testMethod(@Query("age") int age, @Query("name") String name);
}
