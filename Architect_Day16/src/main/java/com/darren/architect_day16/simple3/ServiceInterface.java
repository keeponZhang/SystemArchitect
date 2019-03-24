package com.darren.architect_day16.simple3;

import com.darren.architect_day16.retrofit2.http.FormUrlEncoded;
import com.darren.architect_day16.retrofit2.http.POST;
import com.darren.architect_day16.retrofit2.http.PartMap;

import java.util.Map;

/**
 * Created by hcDarren on 2017/10/15.
 */

public interface ServiceInterface {
    /**
     * 用户登录
     * @return
     */
    @POST
    @FormUrlEncoded
    String userLogin(@PartMap Map<String,Object> params);

    String userRegister();
}
