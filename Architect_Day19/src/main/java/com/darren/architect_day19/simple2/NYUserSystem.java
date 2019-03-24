package com.darren.architect_day19.simple2;


import com.darren.architect_day19.simple2.handler.AbsUserSystemHandler;
import com.darren.architect_day19.simple2.handler.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcDarren on 2017/10/22.
 * 农药系统 - 列表存储
 */
public class NYUserSystem extends AbsUserSystemHandler {
    private List<UserInfo> userInfos;

    public NYUserSystem(){
        userInfos = new ArrayList<>();
        userInfos.add(new UserInfo("Wenld","240336124","001","男"));
        userInfos.add(new UserInfo("yuFrank","240336124","002","男"));
        userInfos.add(new UserInfo("葡萄我爱吃","240336124","003","男"));
    }

    @Override
    public UserInfo queryUserInfo(String userName, String userPwd) {
        // 查询用户信息
        // 自己查询一把，有就返回，没有就交给下一个
        for (UserInfo userInfo : userInfos) {
            if(userInfo.userName.equals(userName)&& userInfo.userPwd.equals(userPwd)){
                return userInfo;
            }
        }

        // 没有就交给下一个
        AbsUserSystemHandler nextHandler = getNextHandler();
        if(nextHandler != null) {
            return nextHandler.queryUserInfo(userName, userPwd);
        }

        return null;
    }
}
