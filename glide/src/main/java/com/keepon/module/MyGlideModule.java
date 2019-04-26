package com.keepon.module;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.keepon.interceptor.ProgressInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class MyGlideModule implements GlideModule {
    @Override
    //更改Glide和配置
    public void applyOptions(Context context, GlideBuilder builder) {
        //尝试使用这个ExternalCacheDiskCacheFactory来替换默认的InternalCacheDiskCacheFactory，从而将所有Glide加载的图片都缓存到SD卡上。
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context));
        //Glide加载图片的默认格式是RGB_565，而Picasso加载图片的默认格式是ARGB_8888
        //ARGB_8888格式的图片效果会更加细腻，但是内存开销会比较大。而RGB_565格式的图片则更加节省内存，但是图片效果上会差一些。
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    //替换Glide组件的
    public void registerComponents(Context context, Glide glide) {
        //register()方法中使用的Map类型来存储已注册的组件，因此我们这里重新注册了一遍GlideUrl.class类型的组件，就把原来的组件给替换掉了。
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        OkHttpClient okHttpClient = builder.build();
        glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(okHttpClient));

    }
}
