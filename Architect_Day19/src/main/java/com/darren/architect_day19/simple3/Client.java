package com.darren.architect_day19.simple3;


import com.darren.architect_day19.simple3.facade.UserSystemFacade;
import com.darren.architect_day19.simple3.handler.UserInfo;

/**
 * Created by hcDarren on 2017/10/22.
 * 一般写法 - 采用迭代器设计模式
 */

public class Client {
    public static void main(String[] args){
        // 门面设计模式
        UserSystemFacade userSystemFacade = new UserSystemFacade();
        UserInfo userInfo = userSystemFacade.queryUserInfo("Wenld","240336124");
        System.out.println(userInfo);
    }
}
