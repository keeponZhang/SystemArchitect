package com.darren.architect_day21.simple2;

import com.darren.architect_day21.simple2.status.OrderOperateStatus;

/**
 * Created by hcDarren on 2017/11/4.
 */

public class BaseOrder {
    protected OrderOperateStatus mStatus;

    public void setStatus(OrderOperateStatus status){
        this.mStatus = status;
    }
}
