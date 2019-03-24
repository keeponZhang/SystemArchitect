package com.darren.architect_day19.simple1;


import com.darren.architect_day19.simple1.iterator.Iterator;

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

        // 两个系统统一放到一个地方 // 责任链模式讲解
        UserInfo loginUserInfo = queryUserInfo("高岩","240",wxUserSystem.iterator());

        // 这个地方做了 if else 判断
        if(loginUserInfo == null) {
            // 从QQ的系统里面去获取
            loginUserInfo = queryUserInfo("高岩", "240", qqUserSystem.iterator());
        }

        // 很有可能会接第三个系统，或者说还有第四个系统
        if(loginUserInfo == null){
            // 从农药里面去查
            loginUserInfo = queryUserInfo("高岩", "240", nyUserSystem.iterator());
        }
        
        if(loginUserInfo == null){
            // 登录失败，用户名和密码错误
        }

        // 再打一个比如，四个人做开发 ，有一哥们要去显示列表 ，
        // 但是数据已经被存入到数据库，而且存在三个库里面
    }

    /**
     * 查询用户信息
     * @param userName
     * @param userPwd
     * @param iterator
     * @return
     */
    private static UserInfo queryUserInfo(String userName, String userPwd, Iterator<UserInfo> iterator) {
        while (iterator.hasNext()){
            UserInfo userInfo = iterator.next();
            if(userInfo.userName.equals(userName) && userInfo.userPwd.equals(userPwd)){
                return userInfo;
            }
        }
        return null;
    }
}
