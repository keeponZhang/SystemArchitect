package com.keepon;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

public class CustomGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //通过builder.setXXX进行配置.
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //通过glide.register进行配置.
    }
}