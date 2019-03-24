package com.darren.architect_day19.simple2.handler;

/**
 * Created by hcDarren on 2017/10/28.
 * 责任链设计模式 - 抽象处理者接口
 */
public interface IUserSystemHandler<T extends IUserSystemHandler> {
     void nextHandler(T systemHandler);
}
