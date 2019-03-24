package com.darren.architect_day19.simple2.handler;

/**
 * Created by hcDarren on 2017/10/28.
 * 责任链设计模式 - 抽象处理者角色
 */
public abstract class AbsUserSystemHandler implements IUserSystemHandler<AbsUserSystemHandler>,IUserSystem{
    private AbsUserSystemHandler nextHandler;

    public AbsUserSystemHandler getNextHandler() {
        return nextHandler;
    }
    @Override
    public void nextHandler(AbsUserSystemHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
}
