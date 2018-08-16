package com.darren.architect_day13.simple2;

/**
 * 策略模式的上下文，有点类似于 android 里面的 Context
 * 可以获取一些额外的基本信息 - 理财有关
 * 这个类有点多余，在实际的开发中可以不写
 * Created by hcDarren on 2017/10/7.
 */
public class FinanceContext {
    private IFinance finance;

    public FinanceContext(IFinance finance){
        this.finance = finance;
    }

    public float finance(int month,int money){
        return finance.finance(month,money);
    }

    // 可以参考系统的服务怎么写的
    /*public IFinance getFinance(){

    }*/
}
