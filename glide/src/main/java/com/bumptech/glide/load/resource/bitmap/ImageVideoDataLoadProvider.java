package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.model.ImageVideoWrapperEncoder;
import com.bumptech.glide.provider.DataLoadProvider;

import java.io.File;
import java.io.InputStream;

/**
 * A {@link com.bumptech.glide.provider.DataLoadProvider} for loading either an {@link InputStream} or an
 * {@link ParcelFileDescriptor} as an {@link Bitmap}.
 */
public class ImageVideoDataLoadProvider implements DataLoadProvider<ImageVideoWrapper, Bitmap> {
    private final ImageVideoBitmapDecoder sourceDecoder;
    private final ResourceDecoder<File, Bitmap> cacheDecoder;
    private final ResourceEncoder<Bitmap> encoder;
    private final ImageVideoWrapperEncoder sourceEncoder;

    // streamBitmapProvider： StreamBitmapDataLoadProvider（这里为什传入的是StreamBitmapDataLoadProvider
    // ，因为ImageVideoWrapper里面有Stream）
    // fileDescriptorBitmapProvider： FileDescriptorBitmapDataLoadProvider
    public ImageVideoDataLoadProvider(DataLoadProvider<InputStream, Bitmap> streamBitmapProvider,
                                      DataLoadProvider<ParcelFileDescriptor, Bitmap> fileDescriptorBitmapProvider) {
        encoder = streamBitmapProvider.getEncoder();
        sourceEncoder = new ImageVideoWrapperEncoder(streamBitmapProvider.getSourceEncoder(),
                fileDescriptorBitmapProvider.getSourceEncoder());
        //streamBitmapProvider:StreamBitmapDataLoadProvider(这里为什么直接获取的是StreamBitmapDataLoadProvider)
        cacheDecoder = streamBitmapProvider.getCacheDecoder();
        //sourceDecoder要注意，这里的泛型跟DataLoadProvider的泛型一样,然后把第一个泛型作为参数，第二个泛型包装成Resource
        //ImageVideoBitmapDecoder本质是从ImageVideoWrapper的InputStream到Bitmap,
        // 所以传入streamBitmapProvider，简单的代理复用
        sourceDecoder = new ImageVideoBitmapDecoder(streamBitmapProvider.getSourceDecoder(),
                fileDescriptorBitmapProvider.getSourceDecoder());
    }

    @Override
    public ResourceDecoder<File, Bitmap> getCacheDecoder() {
        return cacheDecoder;
    }

    @Override
    public ResourceDecoder<ImageVideoWrapper, Bitmap> getSourceDecoder() {
        return sourceDecoder;
    }

    @Override
    public Encoder<ImageVideoWrapper> getSourceEncoder() {
        return sourceEncoder;
    }

    @Override
    public ResourceEncoder<Bitmap> getEncoder() {
        return encoder;
    }
}
