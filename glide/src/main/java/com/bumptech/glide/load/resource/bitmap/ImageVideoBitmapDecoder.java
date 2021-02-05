package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.ImageVideoWrapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link ResourceDecoder} that decodes {@link ImageVideoWrapper}s using
 * a wrapped {@link ResourceDecoder} for {@link InputStream}s
 * and a wrapped {@link ResourceDecoder} for {@link ParcelFileDescriptor}s.
 * The {@link InputStream} data in the {@link ImageVideoWrapper} is always preferred.
 */
public class ImageVideoBitmapDecoder implements ResourceDecoder<ImageVideoWrapper, Bitmap> {
    private static final String TAG = "ImageVideoDecoder";
    private final ResourceDecoder<InputStream, Bitmap> streamDecoder;
    private final ResourceDecoder<ParcelFileDescriptor, Bitmap> fileDescriptorDecoder;

    public ImageVideoBitmapDecoder(ResourceDecoder<InputStream, Bitmap> streamDecoder,
            ResourceDecoder<ParcelFileDescriptor, Bitmap> fileDescriptorDecoder) {
        this.streamDecoder = streamDecoder;
        this.fileDescriptorDecoder = fileDescriptorDecoder;
    }

    @SuppressWarnings("resource")
    // @see ResourceDecoder.decode
    //ImageVideoWrapper变Bitmap，实现首先是取出ImageVideoWrapper的InputStream
    @Override
    public Resource<Bitmap> decode(ImageVideoWrapper source, int width, int height) throws IOException {
        Resource<Bitmap> result = null;
        //先调用了source.getStream()来获取到服务器返回的InputStream
        Log.e("TAG", "ImageVideoBitmapDecoder decode:");
        InputStream is = source.getStream();
        if (is != null) {
            try {
                //调用streamDecoder.decode()方法进行解码
                //streamDecoder:StreamBitmapDecoder ,返回 Resource<Bitmap>对象
                //(真正做事的是streamDecoder)
                result = streamDecoder.decode(is, width, height);
            } catch (IOException e) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Failed to load image from stream, trying FileDescriptor", e);
                }
            }
        }

        if (result == null) {
            ParcelFileDescriptor fileDescriptor = source.getFileDescriptor();
            if (fileDescriptor != null) {
                result = fileDescriptorDecoder.decode(fileDescriptor, width, height);
            }
        }
//        会将值返回到GifBitmapWrapperResourceDecoder的decodeBitmapWrapper()方法当中
        return result;
    }

    @Override
    public String getId() {
        return "ImageVideoBitmapDecoder.com.bumptech.glide.load.resource.bitmap";
    }
}
