package com.darren.architect_day19.simple2;


import com.darren.architect_day19.simple2.handler.AbsUserSystemHandler;
import com.darren.architect_day19.simple2.handler.UserInfo;

/**
 * Created by hcDarren on 2017/10/22.
 * 微信的用户系统 - 数组存储
 */
public class WXUserSystem extends AbsUserSystemHandler {
    UserInfo[] userInfos;

    public WXUserSystem(){
        userInfos = new UserInfo[3];
        userInfos[0] = new UserInfo("大弟子","240336124","001","男");
        userInfos[1] = new UserInfo("AlvinMoon","240336124","002","男");
        userInfos[2] = new UserInfo("高岩","240336124","003","男");
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
