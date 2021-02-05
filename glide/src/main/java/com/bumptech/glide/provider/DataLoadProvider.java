package com.bumptech.glide.provider;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;

import java.io.File;

/**
 * A load provider that provides the necessary encoders and decoders to decode a specific type of resource from a
 * specific type of data.
 *
 * @param <T> The type of data the resource will be decoded from.
 * @param <Z> The type of resource that will be decoded.
 */
//负责编解码，Data是从数据源中获取到的数据,Resource是我们解码后的资源,解码是data到resource的过程，
// 而编码和前面的不一样，是将data和resource持久化到本地的过程
    //实现类当中有今天要讲的LoadProvider
    // A:String T:ImageVideoWrapper Z:GifBitmapWrapper R:GlideDrawable
    //ImageVideoGifDrawableLoadProvider
//前面2个是ResourceDecoder，后面2个是Encoder
public interface DataLoadProvider<T, Z> {

    //解码为什么会有2中呢，一种是从本地，一种是从网络，本地的是File
    /**
     * Returns the {@link ResourceDecoder} to use to decode the resource from the disk cache.
     */
    //其实会从file到InputStream，然后转成GifBitmapWrapper
    ResourceDecoder<File, Z> getCacheDecoder();

    /**
     * Returns the {@link ResourceDecoder} to use to decode the resource from the original data.
     */
    //把ImageVideoWrapper或者里面的inputStream解码成bitmap或者GifDrawable或者GifBitmapWrapper
    ResourceDecoder<T, Z> getSourceDecoder();

    /**
     * Returns the {@link Encoder} to use to write the original data to the disk cache.
     */
    //把GifBitmapWrapper里面的gifResource或者bitmapResource输出到本地
    Encoder<T> getSourceEncoder();

    /**
     * Returns the {@link ResourceEncoder} to use to write the decoded and transformed resource
     * to the disk cache.
     */
    //调用transformed生成的resource输出到本地，注意ResourceEncoder，也是Encoder，本质是输出
    ResourceEncoder<Z> getEncoder();
}
