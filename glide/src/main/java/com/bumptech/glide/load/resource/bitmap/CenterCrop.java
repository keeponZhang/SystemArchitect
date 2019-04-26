package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/**
 * Scale the image so that either the width of the image matches the given width and the height of the image is
 * greater than the given height or vice versa, and then crop the larger dimension to match the given dimension.
 *
 * Does not maintain the image's aspect ratio
 */
public class CenterCrop extends BitmapTransformation {

    public CenterCrop(Context context) {
        super(context);
    }

    public CenterCrop(BitmapPool bitmapPool) {
        super(bitmapPool);
    }

    // Bitmap doesn't implement equals, so == and .equals are equivalent here.
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    //做变换是会调用Transformation的transform(Resource<Bitmap> resource, int outWidth, int outHeight)
    //BitmapTransformation又暴露了transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight);
    //第一个参数pool，这个是Glide中的一个Bitmap缓存池，用于对Bitmap对象进行重用，否则每次图片变换都重新创建Bitmap对象将会非常消耗内存。
    //第二个参数toTransform，这个是原始图片的Bitmap对象，我们就是要对它来进行图片变换
    //第三和第四个参数比较简单，分别代表图片变换后的宽度和高度，其实也就是override()方法中传入的宽和高的值了
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        final Bitmap toReuse = pool.get(outWidth, outHeight, toTransform.getConfig() != null
                ? toTransform.getConfig() : Bitmap.Config.ARGB_8888);
        Bitmap transformed = TransformationUtils.centerCrop(toReuse, toTransform, outWidth, outHeight);
        //在最终返回这个Bitmap对象之前，还会尝试将复用的Bitmap对象重新放回到缓存池当中
        if (toReuse != null && toReuse != transformed && !pool.put(toReuse)) {
            toReuse.recycle();
        }
        return transformed;
    }

    @Override
    public String getId() {
        return "CenterCrop.com.bumptech.glide.load.resource.bitmap";
    }
}
