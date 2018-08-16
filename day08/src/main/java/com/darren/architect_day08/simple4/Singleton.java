package com.darren.architect_day08.simple4;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例设计模式 - 容器管理 系统的服务就是用的这种
 * Created by hcDarren on 2017/9/17.
 */

public class Singleton {
    private static Map<String,Object> mSingleMap = new HashMap<>();

    static {
        mSingleMap.put("activity_manager",new Singleton());
    }

    private Singleton() {

    }

    public static Object getService(String serviceName){
        return mSingleMap.get(serviceName);
    }
}
