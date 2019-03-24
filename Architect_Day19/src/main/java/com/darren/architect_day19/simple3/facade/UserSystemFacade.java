package com.darren.architect_day19.simple3.facade;


import com.darren.architect_day19.simple3.NYUserSystem;
import com.darren.architect_day19.simple3.QQUserSystem;
import com.darren.architect_day19.simple3.WXUserSystem;
import com.darren.architect_day19.simple3.handler.AbsUserSystemHandler;
import com.darren.architect_day19.simple3.handler.IUserSystem;
import com.darren.architect_day19.simple3.handler.UserInfo;

/**
 * Created by hcDarren on 2017/10/22.
 * 门面设计模式 - 易于使用的高层次
 */
public class UserSystemFacade implements IUserSystem {
    // 第一应该检查的系统
    private AbsUserSystemHandler mFirstSystemHandler;

    public UserSystemFacade(){
        // 根据用户名和密码去查询用户信息，
        // 如果没有查询到那么代表登录失败，如果查询到了代表登录成功
        mFirstSystemHandler = new WXUserSystem();
        QQUserSystem qqUserSystem = new QQUserSystem();
        NYUserSystem nyUserSystem = new NYUserSystem();

        mFirstSystemHandler.nextHandler(qqUserSystem);
        qqUserSystem.nextHandler(nyUserSystem);
    }
    @Override
    public UserInfo queryUserInfo(String userName, String userPwd) {
        return mFirstSystemHandler.queryUserInfo(userName,userPwd);
    }
}
