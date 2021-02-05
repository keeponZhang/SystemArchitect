package com.bumptech.glide.provider;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

/**
 * An extension of {@link com.bumptech.glide.provider.DataLoadProvider} that also allows a
 * {@link ModelLoader} and a
 * {@link ResourceTranscoder} to be retrieved.
 *
 * @param <A> The type of model.
 * @param <T> The type of data that will be decoded from.
 * @param <Z> The type of resource that will be decoded.
 * @param <R> The type of resource that the decoded resource will be transcoded to.
 */
//有两个特别的方法
public interface LoadProvider<A, T, Z, R> extends DataLoadProvider<T, Z> {

    /**
     * Returns the {@link ModelLoader} to convert from the given model to a data type.
     */
    ModelLoader<A, T> getModelLoader();

    /**
     * Returns the {@link ResourceTranscoder} to convert from the decoded
     * and transformed resource into the transcoded resource.
     */
    //父类是编码，解码，这个是转码，当显示的数据和缓存的数据不一样的时候可以用
    ResourceTranscoder<Z, R> getTranscoder();
}
