package com.darren.architect_day13.simple1;

/**
 * 一般写法，有什么问题没有？
 * Created by hcDarren on 2017/10/7.
 */
public class FinanceManager {
    public enum Finance{
        ZHI_FU_BAO,REN_ZHONG
        //还有很多，那么就会导致这个类越来越庞大，每种利息都是不一样的，必须新增
    }
    private float zhifubaoFinance(int month, int money) {
        if (month == 3) {
            return money + money * 0.047f / 12 * month;
        }
        if (month == 6) {
            return money + money * 0.05f / 12 * month;
        }
        if (month == 12) {
            return money + money * 0.06f / 12 * month;
        }
        throw new IllegalArgumentException("月份不对");
    }

    private float renzhongFinance(int month, int money) {
        if (month == 3) {
            return money + money * 0.09f / 12 * month;
        }
        if (month == 6) {
            return money + money * 0.112f / 12 * month;
        }
        if (month == 12) {
            return money + money * 0.12f / 12 * month;
        }
        throw new IllegalArgumentException("月份不对");
    }

    /**
     * 获取计算金额
     * @param month
     * @param money
     * @param finance
     * @return
     */
    public float finance(int month, int money,Finance finance){
        switch (finance){
            case REN_ZHONG:
                return renzhongFinance(month,money);
            case ZHI_FU_BAO:
                return zhifubaoFinance(month,money);
            default:
                return 0f;
        }
    }
}
