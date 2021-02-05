package com.bumptech.glide.load.resource.gifbitmap;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.util.ByteArrayPool;

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link ResourceDecoder} that can decode either an {@link Bitmap} or an {@link GifDrawable}
 * from an {@link InputStream} or a {@link android.os.ParcelFileDescriptor ParcelFileDescriptor}.
 */
public class GifBitmapWrapperResourceDecoder implements ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> {
    private static final ImageTypeParser DEFAULT_PARSER = new ImageTypeParser();
    private static final BufferedStreamFactory DEFAULT_STREAM_FACTORY = new BufferedStreamFactory();
    // 2048 is rather arbitrary, for most well formatted image types we only need 32 bytes.
    // Visible for testing.
    static final int MARK_LIMIT_BYTES = 2048;
    //ImageVideoBitmapDecoder
    private final ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder;
    private final ResourceDecoder<InputStream, GifDrawable> gifDecoder;
    private final BitmapPool bitmapPool;
    private final ImageTypeParser parser;
    private final BufferedStreamFactory streamFactory;
    private String id;

    public GifBitmapWrapperResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder,
            ResourceDecoder<InputStream, GifDrawable> gifDecoder, BitmapPool bitmapPool) {
        this(bitmapDecoder, gifDecoder, bitmapPool, DEFAULT_PARSER, DEFAULT_STREAM_FACTORY);
    }

    // Visible for testing.
    GifBitmapWrapperResourceDecoder(ResourceDecoder<ImageVideoWrapper, Bitmap> bitmapDecoder,
            ResourceDecoder<InputStream, GifDrawable> gifDecoder, BitmapPool bitmapPool, ImageTypeParser parser,
            BufferedStreamFactory streamFactory) {
        //这个类比较特殊，会有两个decoder，一般一般加载，你不知道是静态图还是gif
        this.bitmapDecoder = bitmapDecoder;
        this.gifDecoder = gifDecoder;
        this.bitmapPool = bitmapPool;
        this.parser = parser;
        this.streamFactory = streamFactory;
    }

    @SuppressWarnings("resource")
    // @see ResourceDecoder.decode
    @Override
    public Resource<GifBitmapWrapper> decode(ImageVideoWrapper source, int width, int height) throws IOException {
        Log.w("TAG", "Decoder GifBitmapWrapperResourceDecoder decode 从ImageVideoWrapper 到GifBitmapWrapper:");
        ByteArrayPool pool = ByteArrayPool.get();
        byte[] tempBytes = pool.getBytes();

        GifBitmapWrapper wrapper = null;
        try {
            //调了另一个重载方法
            wrapper = decode(source, width, height, tempBytes);
        } finally {
            pool.releaseBytes(tempBytes);
        }
        //又将GifBitmapWrapper封装到了一个GifBitmapWrapperResource对象当中，最终返回的是一个Resource<GifBitmapWrapper>对象
        //回到DecodeJob当中decodeFromSourceData
        return wrapper != null ? new GifBitmapWrapperResource(wrapper) : null;
    }

    private GifBitmapWrapper decode(ImageVideoWrapper source, int width, int height, byte[] bytes) throws IOException {

        final GifBitmapWrapper result;
        if (source.getStream() != null) {
            //调用了decodeStream()方法，准备从服务器返回的流当中读取数据。
            result = decodeStream(source, width, height, bytes);
        } else {
            Log.e("TAG", "********************GifBitmapWrapperResourceDecoder " +
                    "decode这里感觉不会调用啊*********************************:");
            result = decodeBitmapWrapper(source, width, height);
        }
        return result;
    }

    private GifBitmapWrapper decodeStream(ImageVideoWrapper source, int width, int height, byte[] bytes)
            throws IOException {
        InputStream bis = streamFactory.build(source.getStream(), bytes);
        bis.mark(MARK_LIMIT_BYTES);
        ImageHeaderParser.ImageType type = parser.parse(bis);
        bis.reset();

        GifBitmapWrapper result = null;
        //如果是GIF图就调用decodeGifWrapper()方法来进行解码
        if (type == ImageHeaderParser.ImageType.GIF) {
            Log.e("TAG", "GifBitmapWrapperResourceDecoder decodeStream 字节流获取表示是gif:");
            result = decodeGifWrapper(bis, width, height);
        }
        // Decoding the gif may fail even if the type matches.
        if (result == null) {
            // We can only reset the buffered InputStream, so to start from the beginning of the stream, we need to
            // pass in a new source containing the buffered stream rather than the original stream.
            ImageVideoWrapper forBitmapDecoder = new ImageVideoWrapper(bis, source.getFileDescriptor());
            //如果是普通的静图就用调用decodeBitmapWrapper()方法来进行解码
            result = decodeBitmapWrapper(forBitmapDecoder, width, height);
        }
        return result;
    }

    private GifBitmapWrapper decodeGifWrapper(InputStream bis, int width, int height) throws IOException {
        GifBitmapWrapper result = null;
        Resource<GifDrawable> gifResource = gifDecoder.decode(bis, width, height);
        if (gifResource != null) {
            GifDrawable drawable = gifResource.get();
            // We can more efficiently hold Bitmaps in memory, so for static GIFs, try to return Bitmaps
            // instead. Returning a Bitmap incurs the cost of allocating the GifDrawable as well as the normal
            // Bitmap allocation, but since we can encode the Bitmap out as a JPEG, future decodes will be
            // efficient.
            //gif只有一帧的话，也当静态图处理
            if (drawable.getFrameCount() > 1) {
                result = new GifBitmapWrapper(null /*bitmapResource*/, gifResource);
            } else {
                Resource<Bitmap> bitmapResource = new BitmapResource(drawable.getFirstFrame(), bitmapPool);
                result = new GifBitmapWrapper(bitmapResource, null /*gifResource*/);
            }
        }
        return result;
    }

    private GifBitmapWrapper decodeBitmapWrapper(ImageVideoWrapper toDecode, int width, int height) throws IOException {
        GifBitmapWrapper result = null;
//        bitmapDecoder: ImageVideoBitmapDecoder(这个是FixedLoadProvider的一个参数)
        //解码自然要用解码器，这里已经确定了是静态图了，所有了bitmapDecoder，解码成 Resource<Bitmap>
        Resource<Bitmap> bitmapResource = bitmapDecoder.decode(toDecode, width, height);
        //又将Resource<Bitmap>封装到了一个GifBitmapWrapper对象当中。这个GifBitmapWrapper顾名思义，就是既能封装GIF，又能封装Bitmap，
        // 从而保证了不管是什么类型的图片Glide都能从容应对。
        //然后这个GifBitmapWrapper对象会一直向上返回，返回到GifBitmapWrapperResourceDecoder最外层的decode()方法的时候，
        // 会对它再做一次封装成GifBitmapWrapperResource（66行）
        if (bitmapResource != null) {
            result = new GifBitmapWrapper(bitmapResource, null);
        }

        return result;
    }

    @Override
    public String getId() {
        if (id == null) {
            id = gifDecoder.getId() + bitmapDecoder.getId();
        }
        return id;
    }

    // Visible for testing.
    static class BufferedStreamFactory {
        public InputStream build(InputStream is, byte[] buffer) {
            return new RecyclableBufferedInputStream(is, buffer);
        }
    }

    // Visible for testing.
    static class ImageTypeParser {
        public ImageHeaderParser.ImageType parse(InputStream is) throws IOException {
            return new ImageHeaderParser(is).getType();
        }
    }
}
