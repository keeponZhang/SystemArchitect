package com.bumptech.glide.load.resource.transcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawableResource;

/**
 * An {@link ResourceTranscoder} that converts
 * {@link Bitmap}s into {@link android.graphics.drawable.BitmapDrawable}s.
 */
public class GlideBitmapDrawableTranscoder implements ResourceTranscoder<Bitmap, GlideBitmapDrawable> {
    private final Resources resources;
    private final BitmapPool bitmapPool;

    public GlideBitmapDrawableTranscoder(Context context) {
        this(context.getResources(), Glide.get(context).getBitmapPool());
    }

    public GlideBitmapDrawableTranscoder(Resources resources, BitmapPool bitmapPool) {
        this.resources = resources;
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<GlideBitmapDrawable> transcode(Resource<Bitmap> toTranscode) {
        Log.e("TAG", "!!!!!!!GlideBitmapDrawableTranscoder transcode 转码看过来，此时调用了asBitmap:");
//        这里new出了一个GlideBitmapDrawable对象，并把Bitmap封装到里面。然后对GlideBitmapDrawable再进行一次封装，
        // 返回一个Resource<GlideBitmapDrawable>对象
        GlideBitmapDrawable drawable = new GlideBitmapDrawable(resources, toTranscode.get());
        //再返回到GifBitmapWrapperDrawableTranscoder的transcode()方法中
        return new GlideBitmapDrawableResource(drawable, bitmapPool);
    }

    @Override
    public String getId() {
        return "GlideBitmapDrawableTranscoder.com.bumptech.glide.load.resource.transcode";
    }
}
