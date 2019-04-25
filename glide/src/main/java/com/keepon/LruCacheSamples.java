package com.keepon;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class LruCacheSamples {

    private static final int MAX_SIZE = 50;
    public static final String TAG = "LruCacheSample";
    public static  void hashMapAndLinkedHashMap(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("a3", "aa");
        map.put("a2", "bb");
        map.put("b1", "cc");

        for (Iterator iterator = map.values().iterator(); iterator.hasNext();)     {

            String name = (String) iterator.next();

            System.out.println(name);

        }


        Map<String, String> map2 = new LinkedHashMap<String, String>();

        map2.put("a3", "aa");

        map2.put("a2", "bb");

        map2.put("b1", "cc");

        for (Iterator iterator = map2.values().iterator(); iterator.hasNext();) {

            String name = (String) iterator.next();

            System.out.println(name);
        }
//        linkedMap在于存储数据你想保持进入的顺序与被取出的顺序一致的话，优先考虑LinkedMap，hashMap键只能允许为一条为空，value可以允许为多条为空，键唯一，但值可以多个。
//        经本人测试linkedMap键和值都不可以为空
    }
    public static void startRun() {



        LruCacheSample sample = new LruCacheSample();
        Log.e("LruCacheSample", "Start Put Object1, size=" + sample.size());
        sample.put("Object1", new Holder("Object1", 10));

        Log.e(TAG, "Start Put Object2, size=" + sample.size());
        sample.put("Object2", new Holder("Object2", 20));

        Log.e(TAG, "Start Put Object3, size=" + sample.size());
        sample.put("Object3", new Holder("Object3", 20));
        Log.e(TAG, "Start Put Object3_2, size=" + sample.size());
        sample.put("Object3", new Holder("Object3", 30));

        Log.e(TAG, "Start Put Object4, size=" + sample.size());
        sample.put("Object4", new Holder("Object4", 10));



    }
    private void addBefore() {
        //after是next节点，before是头结点
        //新插入的节点的next节点指向header
        //            after  = existingEntry;
        // existingEntry.before是最后一个插入节点
        //新插入的节点的pre节点指向原最后插入的一个节点(新插入的节点after和before搞定)
        //            before = existingEntry.before;
        //before现在是倒数第二个节点
        //原最后一个节点的after原本指向的是header，现在变成了倒数第二个节点，
        // 它的after就指向了现在刚插入的节点
        //            before.after = this;
        //after现在是header节点，现在指向最后一个顶点
        //            after.before = this;
    }
    static class LruCacheSample extends LruCache<String, Holder> {

        LruCacheSample() {
            super(MAX_SIZE);
        }


        @Override
        protected int sizeOf(String key, Holder value) {
            return value.getSize();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Holder oldValue, Holder newValue) {
            if (oldValue != null) {
                Log.e(TAG, "remove=" + oldValue.getName());
            }
            if (newValue != null) {
                Log.e(TAG, "add=" + newValue.getName());
            }
        }
    }

    static class Holder {

        private String mName;
        private int mSize;

        Holder(String name, int size) {
            mName = name;
            mSize = size;
        }

        public String getName() {
            return mName;
        }

        public int getSize() {
            return mSize;
        }
    }
}