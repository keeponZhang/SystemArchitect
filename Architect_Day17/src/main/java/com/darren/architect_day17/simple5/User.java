package com.darren.architect_day17.simple5;

/**
 * Created by hcDarren on 2017/10/21.
 * 用户的对象
 */
public class User implements Cloneable{
    public String userName;
    public int age;
    public Address userAddress;

    @Override
    protected User clone() throws CloneNotSupportedException {
        User user = (User) super.clone();
        // 把地址也做一次克隆，达到深拷贝
        user.userAddress = userAddress.clone();
        return user;
    }
}
