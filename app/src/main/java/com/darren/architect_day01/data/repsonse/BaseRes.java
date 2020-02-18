package com.darren.architect_day01.data.repsonse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhanghanguo@yy.com on 2015/9/28.
 */
public class BaseRes {

    @SerializedName(value = "statusDefault",alternate = {"status"})
    public Status mStatus;

    public boolean isSuccessful() {
        return mStatus != null && mStatus.code == 0;
    }

    public String getMessage() {
        if (mStatus != null) {
            return mStatus.msg + "[" + mStatus.code + "]";
        } else {
            return "unknown error!";
        }
    }
}
