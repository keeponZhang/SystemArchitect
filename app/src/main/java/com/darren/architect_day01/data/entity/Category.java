package com.darren.architect_day01.data.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Category {
    //@Expose
    @Expose
    public int id;
    @Expose
    public String name;
    @Expose
    public List<Category> children;
    //因业务需要增加，但并不需要序列化
    //不需要序列化,所以不加 @Expose 注解，
    //等价于 @Expose(deserialize = false,serialize = false)
    public Category parent;
}