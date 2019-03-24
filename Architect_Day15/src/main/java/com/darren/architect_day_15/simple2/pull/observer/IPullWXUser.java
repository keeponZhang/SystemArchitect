package com.darren.architect_day_15.simple2.pull.observer;

import com.darren.architect_day_15.simple2.pull.observable.PullWXPublicObservable;

/**
 * Created by hcDarren on 2017/10/14.
 * 微信公众号 - 微信用户
 */
public interface IPullWXUser {
    /**
     * 把公众号给用户
     */
    public void pull(PullWXPublicObservable wxObservable);
}
