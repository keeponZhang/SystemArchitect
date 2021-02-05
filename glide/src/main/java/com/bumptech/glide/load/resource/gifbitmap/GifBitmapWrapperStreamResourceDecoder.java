package com.bumptech.glide.load.resource.gifbitmap;

import android.util.Log;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.ImageVideoWrapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link com.bumptech.glide.load.ResourceDecoder} that can decode an
 * {@link GifBitmapWrapper} from {@link InputStream} data.
 */
public class GifBitmapWrapperStreamResourceDecoder implements ResourceDecoder<InputStream, GifBitmapWrapper> {
    // GifBitmapWrapperResourceDecoder
    private final ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> gifBitmapDecoder;

    public GifBitmapWrapperStreamResourceDecoder(
            ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> gifBitmapDecoder) {
        this.gifBitmapDecoder = gifBitmapDecoder;
    }

    @Override
    public Resource<GifBitmapWrapper> decode(InputStream source, int width, int height) throws IOException {
        //gifBitmapDecoder:GifBitmapWrapperResourceDecoder<ImageVideoWrapper, GifBitmapWrapper>,
        // 所以这里需要转一下（用到缓存时才会用到）
        Log.e("TAG", "Decoder GifBitmapWrapperStreamResourceDecoder decode 从InputStream " +
                "到GifBitmapWrapper   类型gifBitmapDecoder:"+gifBitmapDecoder);
        //这里又一个装饰者，真正干活的是GifBitmapWrapperResourceDecoder,
        // GifBitmapWrapperResourceDecoder需要的是从从ImageVideoWrapper到到GifBitmapWrapper，所以需要包装系
        return gifBitmapDecoder.decode(new ImageVideoWrapper(source, null), width, height);
    }

    @Override
    public String getId() {
        return gifBitmapDecoder.getId();
    }
}
