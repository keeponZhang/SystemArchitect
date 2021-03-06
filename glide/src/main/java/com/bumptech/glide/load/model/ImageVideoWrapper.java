package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;

import java.io.InputStream;

/**
 * A simple wrapper that wraps an {@link InputStream} and/or an {@link ParcelFileDescriptor}.
 */
public class ImageVideoWrapper {
    //这个Stream是网络返回来的图片流stream
    private final InputStream streamData;
    private final ParcelFileDescriptor fileDescriptor;

    public ImageVideoWrapper(InputStream streamData, ParcelFileDescriptor fileDescriptor) {
        this.streamData = streamData;
        this.fileDescriptor = fileDescriptor;
    }

    public InputStream getStream() {
        return streamData;
    }

    public ParcelFileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }
}
