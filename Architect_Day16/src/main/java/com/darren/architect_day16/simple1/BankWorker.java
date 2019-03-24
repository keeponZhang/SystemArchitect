package com.darren.architect_day16.simple1;

/**
 * Created by hcDarren on 2017/10/15.
 * 银行办理业务 - 代理对象 - 银行的业务员
 */
public class BankWorker implements IBank{
    private IBank bank;
    /**
     * 持有被代理的对象
     * @param bank
     */
    public BankWorker(IBank bank){
        this.bank = bank;
    }

    @Override
    public void applyBank() {
        System.out.println("开始受理");
        bank.applyBank();
        System.out.println("操作完毕");
    }

    @Override
    public void lostBank() {
        System.out.println("开始受理");
        bank.lostBank();
        System.out.println("操作完毕");
    }
}
