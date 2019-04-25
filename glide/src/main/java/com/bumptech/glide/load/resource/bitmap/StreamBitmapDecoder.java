package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.io.InputStream;

/**
 * An {@link com.bumptech.glide.load.ResourceDecoder} that uses an
 * {@link Downsampler} to decode an {@link Bitmap} from an
 * {@link InputStream}.
 */
public class StreamBitmapDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private static final String ID = "StreamBitmapDecoder.com.bumptech.glide.load.resource.bitmap";
    private final Downsampler downsampler;
    private BitmapPool bitmapPool;
    private DecodeFormat decodeFormat;
    private String id;

    public StreamBitmapDecoder(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public StreamBitmapDecoder(BitmapPool bitmapPool) {
        this(bitmapPool, DecodeFormat.DEFAULT);
    }

    public StreamBitmapDecoder(Context context, DecodeFormat decodeFormat) {
        this(Glide.get(context).getBitmapPool(), decodeFormat);
    }

    public StreamBitmapDecoder(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this(Downsampler.AT_LEAST, bitmapPool, decodeFormat);
    }

    public StreamBitmapDecoder(Downsampler downsampler, BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this.downsampler = downsampler;
        this.bitmapPool = bitmapPool;
        this.decodeFormat = decodeFormat;
    }

    @Override
    public Resource<Bitmap> decode(InputStream source, int width, int height) {
//        调用了Downsampler的decode()方法，返回的是bitmap对象
        Bitmap bitmap = downsampler.decode(source, bitmapPool, width, height, decodeFormat);
        //又调用了BitmapResource.obtain()方法，将Bitmap对象包装成了Resource<Bitmap>对象
        //StreamBitmapDecoder会将值返回到ImageVideoBitmapDecoder当中
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    @Override
    public String getId() {
        if (id == null) {
            id = new StringBuilder()
                .append(ID)
                .append(downsampler.getId())
                .append(decodeFormat.name())
                .toString();
        }
        return id;
    }
}
