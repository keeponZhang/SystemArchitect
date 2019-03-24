package com.darren.architect_day16.simple1;

/**
 * Created by hcDarren on 2017/10/15.
 * 银行办理业务 - 被代理的对象 - 我们
 */
public class Man implements IBank{
    private String name;

    public Man(String name){
        this.name = name;
    }

    /**
     * 自己的一些操作
     */
    @Override
    public void applyBank() {
        System.out.println(name + " 申请办卡");
    }

    @Override
    public void lostBank() {
        System.out.println(name + " 申请挂失");
    }
}
