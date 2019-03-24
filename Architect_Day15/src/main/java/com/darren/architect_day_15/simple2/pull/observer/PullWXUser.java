package com.darren.architect_day_15.simple2.pull.observer;

import com.darren.architect_day_15.simple2.pull.observable.PullWXAdvanceObservable;
import com.darren.architect_day_15.simple2.pull.observable.PullWXPublicObservable;

/**
 * Created by hcDarren on 2017/10/14.
 * 微信公众号 - 具体订阅用户（Darren，高岩）
 */

public class PullWXUser implements IPullWXUser {

    private String name;

    public PullWXUser(String name) {
        this.name = name;
    }

    @Override
    public void pull(PullWXPublicObservable wxObservable) {
        // 拉模式 - 主动的从公众号中拉取一篇文章
        PullWXAdvanceObservable advanceObservable = (PullWXAdvanceObservable) wxObservable;
        System.out.println(name + " 主动拉去一篇文章：" + advanceObservable.getArticle());
    }
}
