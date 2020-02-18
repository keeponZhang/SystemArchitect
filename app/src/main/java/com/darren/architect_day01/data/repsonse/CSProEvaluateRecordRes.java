package com.darren.architect_day01.data.repsonse;

import com.darren.architect_day01.data.entity.CSProEvaluateRecordBean;

import java.util.List;

public class CSProEvaluateRecordRes extends BaseRes {

    /**
     * attachments : {}
     * data : [{"goodsId":0,"needToDo":true,"recommendation":0,"type":0,"typeDesc":"","typeName":"","uid":0,"userAnswerId":0}]
     * status : {"cip":"","code":0,"msg":"","signature":"","sip":"","time":"","tips":""}
     * success : true
     */

    private List<CSProEvaluateRecordBean> data;

    public List<CSProEvaluateRecordBean> getData() {
        return data;
    }

    public void setData(List<CSProEvaluateRecordBean> data) {
        this.data = data;
    }

}
