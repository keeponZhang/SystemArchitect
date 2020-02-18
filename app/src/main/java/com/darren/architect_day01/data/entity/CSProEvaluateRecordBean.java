package com.darren.architect_day01.data.entity;

import java.io.Serializable;

/**
 * createBy	 keepon
 */
public class CSProEvaluateRecordBean implements Serializable {
    /**
     * goodsId : 0
     * needToDo : true
     * recommendation : 0
     * type : 0
     * typeDesc :
     * typeName :
     * uid : 0
     * userAnswerId : 0
     */

    private int goodsId;
    private boolean needToDo;
    private int recommendation;
    private int type;
    private String typeDesc;
    private String typeName;
    private long uid;
    private long userAnswerId;

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public boolean isNeedToDo() {
        return needToDo;
    }

    public void setNeedToDo(boolean needToDo) {
        this.needToDo = needToDo;
    }

    public int getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(int recommendation) {
        this.recommendation = recommendation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(long userAnswerId) {
        this.userAnswerId = userAnswerId;
    }
}
