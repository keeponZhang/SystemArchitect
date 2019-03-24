package com.darren.architect_day18.simple1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcDarren on 2017/10/22.
 * QQ用户系统 - 列表存储
 */
public class QQUserSystem {
    private List<UserInfo> userInfos;

    public QQUserSystem(){
        userInfos = new ArrayList<>();
        userInfos.add(new UserInfo("Darren","240336124","001","男"));
        userInfos.add(new UserInfo("夕决","240336124","002","男"));
        userInfos.add(new UserInfo("yjy239","240336124","003","男"));
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }
}
