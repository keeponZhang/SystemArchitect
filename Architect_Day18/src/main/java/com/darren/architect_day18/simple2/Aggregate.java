package com.darren.architect_day18.simple2;

import com.darren.architect_day18.simple2.iterator.Iterator;

/**
 * Created by hcDarren on 2017/10/22.
 * 容器的接口
 */
public interface Aggregate<T> {
    // Aggregate 离开 Iterator 还可以用吗？不能用 ，
    // 整体 Aggregate（不能用） 局部 Iterator（可以存在）
    Iterator<T> iterator();
}
