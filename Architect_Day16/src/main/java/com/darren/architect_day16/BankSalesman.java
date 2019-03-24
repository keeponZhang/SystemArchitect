package com.darren.architect_day16;

/**
 * description: 静态代理设计模式 - 代理对象
 * author: Darren on 2017/10/11 12:52
 * email: 240336124@qq.com
 * version: 1.0
 */
public class BankSalesman implements IBank{
    private IBank bank;

    public BankSalesman(IBank bank){
        this.bank = bank;
    }

    @Override
    public void applyBank() {
        System.out.println("数据统计");
        bank.applyBank();
        System.out.println("完毕");
    }
}
