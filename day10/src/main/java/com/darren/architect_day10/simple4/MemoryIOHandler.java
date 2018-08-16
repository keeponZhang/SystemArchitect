package com.darren.architect_day10.simple4;

import android.util.LruCache;

/**
 * Created by hcDarren on 2017/9/24.
 */

public class MemoryIOHandler implements IOHandler {
    // 存在运行内存里面，原理是什么？其实就是 Map 集合
    private static LruCache<String,Object> mCache = new LruCache<>(10*1024*1024);// 1 / 8
    @Override
    public void save(String key, String value) {
        mCache.put(key,value);
    }

    @Override
    public void save(String key, double value) {

    }

    @Override
    public void save(String key, int value) {

    }

    @Override
    public void save(String key, long value) {

    }

    @Override
    public void save(String key, boolean value) {

    }

    @Override
    public void save(String key, Object value) {

    }

    @Override
    public String getString(String key) {
        return (String) mCache.get(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return 0;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return false;
    }

    @Override
    public Object getObject(String key) {
        return null;
    }
}
