package com.bumptech.glide.load.resource.transcode;

import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;

/**
 * An {@link ResourceTranscoder} that can transcode either an
 * {@link Bitmap} or an {@link com.bumptech.glide.load.resource.gif.GifDrawable} into an
 * {@link android.graphics.drawable.Drawable}.
 */
//GifBitmapWrapperDrawableTranscoder的核心作用就是用来转码的
public class GifBitmapWrapperDrawableTranscoder implements ResourceTranscoder<GifBitmapWrapper, GlideDrawable> {
    private final ResourceTranscoder<Bitmap, GlideBitmapDrawable> bitmapDrawableResourceTranscoder;

    public GifBitmapWrapperDrawableTranscoder(
            ResourceTranscoder<Bitmap, GlideBitmapDrawable> bitmapDrawableResourceTranscoder) {
        this.bitmapDrawableResourceTranscoder = bitmapDrawableResourceTranscoder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Resource<GlideDrawable> transcode(Resource<GifBitmapWrapper> toTranscode) {
//        transcode()方法先从Resource<GifBitmapWrapper>中取出GifBitmapWrapper对象
        GifBitmapWrapper gifBitmap = toTranscode.get();
        // 然后再从GifBitmapWrapper中取出Resource<Bitmap>对象
        Resource<Bitmap> bitmapResource = gifBitmap.getBitmapResource();

        final Resource<? extends GlideDrawable> result;
        if (bitmapResource != null) {
            //如果Resource<Bitmap>不为空，那么就需要再做一次转码，将Bitmap转换成Drawable对象才行，因为要保证静图和动图的类型一致性，不然逻辑上是不好处理的。
            //bitmapDrawableResourceTranscoder:GlideBitmapDrawableTranscoder
            result = bitmapDrawableResourceTranscoder.transcode(bitmapResource);
        } else {
//            如果Resource<Bitmap>为空，那么说明此时加载的是GIF图
            result = gifBitmap.getGifResource();
        }
        // This is unchecked but always safe, anything that extends a Drawable can be safely cast to a Drawable.
        //因为不管是静图的Resource<GlideBitmapDrawable>对象，还是动图的Resource<GifDrawable>对象，它们都是属于父类Resource<GlideDrawable>对象的
        //Resource<GlideDrawable>其实也就是转换过后的Resource<Z>了
        //继续回到DecodeJob当中，它的decodeFromSource()方法得到了Resource<Z>对象
        return (Resource<GlideDrawable>) result;
    }

    @Override
    public String getId() {
        return "GifBitmapWrapperDrawableTranscoder.com.bumptech.glide.load.resource.transcode";
    }
}
