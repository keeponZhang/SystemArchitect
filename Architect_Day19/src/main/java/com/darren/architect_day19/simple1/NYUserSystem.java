package com.darren.architect_day19.simple1;


import com.darren.architect_day19.simple1.iterator.Iterator;
import com.darren.architect_day19.simple1.iterator.QQIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcDarren on 2017/10/22.
 * 农药系统 - 列表存储
 */
public class NYUserSystem implements Aggregate<UserInfo>{
    private List<UserInfo> userInfos;

    public NYUserSystem(){
        userInfos = new ArrayList<>();
        userInfos.add(new UserInfo("Wenld","240336124","001","男"));
        userInfos.add(new UserInfo("yuFrank","240336124","002","男"));
        userInfos.add(new UserInfo("葡萄我爱吃","240336124","003","男"));
    }

    @Override
    public Iterator<UserInfo> iterator() {
        return new QQIterator(userInfos);
    }
}
