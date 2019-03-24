package com.darren.architect_day19.simple2;


import com.darren.architect_day19.simple2.handler.UserInfo;

/**
 * Created by hcDarren on 2017/10/22.
 * 一般写法 - 采用迭代器设计模式
 */

public class Client {
    public static void main(String[] args){
        // 根据用户名和密码去查询用户信息，
        // 如果没有查询到那么代表登录失败，如果查询到了代表登录成功
        WXUserSystem wxUserSystem = new WXUserSystem();
        QQUserSystem qqUserSystem = new QQUserSystem();
        NYUserSystem nyUserSystem = new NYUserSystem();

        wxUserSystem.nextHandler(qqUserSystem);
        qqUserSystem.nextHandler(nyUserSystem);

        UserInfo userInfo = wxUserSystem.queryUserInfo("Wenld","240");
        System.out.println(userInfo);

        // 发现代码有问题，想想有没有设计模式可以解决

        // 再打一个比如，四个人做开发 ，有一哥们要去显示列表 ，
        // 但是数据已经被存入到数据库，而且存在三个库里面
    }
}
