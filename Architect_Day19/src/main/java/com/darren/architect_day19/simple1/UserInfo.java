package com.darren.architect_day19.simple1;

/**
 * Created by hcDarren on 2017/10/22.
 * 用户信息实体
 */

public class UserInfo {
    public String userName;
    public String userPwd;
    public String userId;
    public String userSex;

    public UserInfo(String userName,String userPwd,String userId,String userSex){
        this.userName = userName;
        this.userId  = userId;
        this.userPwd = userPwd;
        this.userSex = userSex;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", userId='" + userId + '\'' +
                ", userSex='" + userSex + '\'' +
                '}';
    }
}
