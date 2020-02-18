package com.darren.architect_day01.data.entity;

import com.google.gson.annotations.SerializedName;

public class User {
    //省略其它
    @SerializedName(value = "name1",alternate = {"name"})
    public String name;
}