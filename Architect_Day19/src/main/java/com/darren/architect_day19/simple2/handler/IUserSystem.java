package com.darren.architect_day19.simple2.handler;

/**
 * Created by hcDarren on 2017/10/28.
 * 检验用户的处理接口
 */
public interface IUserSystem {
    /**
     * 根据用户名和密码查询用户信息
     * @param userName
     * @param userPwd
     * @return
     */
    public UserInfo queryUserInfo(String userName, String userPwd);
}
