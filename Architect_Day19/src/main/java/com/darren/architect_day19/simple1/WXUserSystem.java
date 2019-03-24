package com.darren.architect_day19.simple1;


import com.darren.architect_day19.simple1.iterator.Iterator;
import com.darren.architect_day19.simple1.iterator.WXIterator;

/**
 * Created by hcDarren on 2017/10/22.
 * 微信的用户系统 - 数组存储
 */
public class WXUserSystem implements Aggregate<UserInfo>{
    UserInfo[] userInfos;

    public WXUserSystem(){
        userInfos = new UserInfo[3];
        userInfos[0] = new UserInfo("大弟子","240336124","001","男");
        userInfos[1] = new UserInfo("AlvinMoon","240336124","002","男");
        userInfos[2] = new UserInfo("高岩","240336124","003","男");
    }

    @Override
    public Iterator<UserInfo> iterator() {
        return new WXIterator(userInfos);
    }
}
