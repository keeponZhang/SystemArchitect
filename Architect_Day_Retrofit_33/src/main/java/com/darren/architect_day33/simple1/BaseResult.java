package com.darren.architect_day33.simple1;

/**
 * Created by hcDarren on 2017/12/16.
 */

public class BaseResult {
    public String code;
    public String msg;

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public boolean isOk(){
        return "0000".equals(code);
    }
}
