package com.darren.architect_day_15.simple2.push.observable;

/**
 * Created by hcDarren on 2017/10/14.
 * 微信公众号 - Android进阶之旅公众号
 */

public class PushWXAdvanceObservable extends PushWXPublicObservable {
    private String article;

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
        // 通知更新,推送给微信用户
        update(article);
    }
}
