package com.bumptech.glide.load.engine;

import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.provider.DataLoadProvider;
import com.bumptech.glide.util.LogTime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class responsible for decoding resources either from cached data or from the original source and applying
 * transformations and transcodes.
 *
 * @param <A> The type of the source data the resource can be decoded from.
 * @param <T> The type of resource that will be decoded.
 * @param <Z> The type of resource that will be transcoded from the decoded and transformed resource.
 */
// A:ImageVideoWrapper T:GifBitmapWrapper Z:GlideDrawable
class DecodeJob<A, T, Z> {
    private static final String TAG = "DecodeJob";
    private static final FileOpener DEFAULT_FILE_OPENER = new FileOpener();

    private final EngineKey resultKey;
    private final int width;
    private final int height;
    //fetcher:ImageVideoFetcher
    private final DataFetcher<A> fetcher;
    //    loadProvider:FixedLoadProvider
    private final DataLoadProvider<A, T> loadProvider;
    private final Transformation<T> transformation;
    //transcoder:GifBitmapWrapperDrawableTranscoder
    private final ResourceTranscoder<T, Z> transcoder;
    private final DiskCacheProvider diskCacheProvider;
    private final DiskCacheStrategy diskCacheStrategy;
    private final Priority priority;
    private final FileOpener fileOpener;

    private volatile boolean isCancelled;

    // A:ImageVideoWrapper T:GifBitmapWrapper Z:GlideDrawable
    public DecodeJob(EngineKey resultKey, int width, int height, DataFetcher<A> fetcher,
                     DataLoadProvider<A, T> loadProvider, Transformation<T> transformation,
                     ResourceTranscoder<T, Z> transcoder,
                     DiskCacheProvider diskCacheProvider, DiskCacheStrategy diskCacheStrategy,
                     Priority priority) {
        this(resultKey, width, height, fetcher, loadProvider, transformation, transcoder,
                diskCacheProvider,
                diskCacheStrategy, priority, DEFAULT_FILE_OPENER);
    }

    //fetcher:ImageVideoFetcher  loadProvider:FixedLoadProvider
    //transcoder:GifBitmapWrapperDrawableTranscoder
    // Visible for testing.
    DecodeJob(EngineKey resultKey, int width, int height, DataFetcher<A> fetcher,
              DataLoadProvider<A, T> loadProvider, Transformation<T> transformation,
              ResourceTranscoder<T, Z> transcoder,
              DiskCacheProvider diskCacheProvider, DiskCacheStrategy diskCacheStrategy,
              Priority priority, FileOpener
                      fileOpener) {
        this.resultKey = resultKey;
        this.width = width;
        this.height = height;
        this.fetcher = fetcher;
        this.loadProvider = loadProvider;
        //transformation做变换用的，如CenterCrop
        this.transformation = transformation;
        this.transcoder = transcoder;
        this.diskCacheProvider = diskCacheProvider;
        this.diskCacheStrategy = diskCacheStrategy;
        this.priority = priority;
        this.fileOpener = fileOpener;
    }

    /**
     * Returns a transcoded resource decoded from transformed resource data in the disk cache, or null if no such
     * resource exists.
     *
     * @throws Exception
     */
    public Resource<Z> decodeResultFromCache() throws Exception {
        if (!diskCacheStrategy.cacheResult()) {
            return null;
        }

        long startTime = LogTime.getLogTime();
//        调用了loadFromCache()方法从缓存当中读取数据
        Log.d("TAG",
                "DecodeJob decodeResultFromCache 准备调用loadFromCache，传入resultKey:" + resultKey);
        Resource<T> transformed = loadFromCache(resultKey);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Decoded transformed from cache", startTime);
        }
        startTime = LogTime.getLogTime();
        Log.w("TAG",
                "DecodeJob decodeResultFromCache（表示缓存拿的也需要转码） 准备调用transcode转码前 transformed:" +
                        transformed);
        Resource<Z> result = transcode(transformed);
        Log.w("TAG",
                "DecodeJob decodeResultFromCache（表示缓存拿的也需要转码） 准备调用transcode转码后 result:" + result);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Transcoded transformed from cache", startTime);
        }
        return result;
    }

    /**
     * Returns a transformed and transcoded resource decoded from source data in the disk cache, or null if no such
     * resource exists.
     *
     * @throws Exception
     */
    public Resource<Z> decodeSourceFromCache() throws Exception {
        if (!diskCacheStrategy.cacheSource()) {
            return null;
        }

        long startTime = LogTime.getLogTime();
        //如果我们是缓存的原始图片，其实并不需要这么多的参数，因为不用对图片做任何的变化
        Log.e("TAG", "DecodeJob decodeSourceFromCache 准备调用loadFromCache，传入orginalKey:" +
                resultKey.getOriginalKey());
        //这里返回的是个T类型的，真正返回的还需要转码下
        Resource<T> decoded = loadFromCache(resultKey.getOriginalKey());
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Decoded source from cache", startTime);
        }
//        如果是decodeSourceFromCache()方法，还要调用一下transformEncodeAndTranscode()方法先将数据转换一下再解码并返回
        return transformEncodeAndTranscode(decoded);
    }

    /**
     * Returns a transformed and transcoded resource decoded from source data, or null if no source data could be
     * obtained or no resource could be decoded.
     *
     * <p>
     * Depending on the {@link com.bumptech.glide.load.engine.DiskCacheStrategy} used, source data is either decoded
     * directly or first written to the disk cache and then decoded from the disk cache.
     * </p>
     *
     * @throws Exception
     */
    // A:ImageVideoWrapper T:GifBitmapWrapper Z:GlideDrawable
    public Resource<Z> decodeFromSource() throws Exception {
//        调用decodeSource()方法来获得一个Resource对象
        //decodeSource()顾名思义是用来解析原图片的
        Resource<T> decoded = decodeSource();
        //调用transformEncodeAndTranscode()方法来处理这个Resource对象
        //而transformEncodeAndTranscode()则是用来对图片进行转换和转码的
        //就把Resource<T>对象转换成Resource<Z>对象了
        //当然也就是Resource<GlideDrawable>对象
        //继续向上返回会回到EngineRunnable的decodeFromSource()方法
        return transformEncodeAndTranscode(decoded);
    }

    public void cancel() {
        isCancelled = true;
        fetcher.cancel();
    }

    //A:ImageVideoWrapper T:GifBitmapWrapper Z:GlideDrawable
    //decoded：class com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperResource
    private Resource<Z> transformEncodeAndTranscode(Resource<T> decoded) {
        long startTime = LogTime.getLogTime();
        //原图一般的话要进行缩放，在该方法处理
        Log.w("TAG", "DecodeJob transformEncodeAndTranscode transform前:" + decoded);
        Resource<T> transformed = transform(decoded);
        Log.w("TAG", "DecodeJob transformEncodeAndTranscode transform后:" + transformed);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Transformed resource from source", startTime);
        }

        writeTransformedToCache(transformed);

        startTime = LogTime.getLogTime();
//        又是调用了transcode()方法
        //Z：GlideBitmapDrawable
        //result:class com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawableResource
        Log.d("TAG", "DecodeJob transformEncodeAndTranscode transcode前:" + decoded);
        Resource<Z> result = transcode(transformed);
        Log.d("TAG", "DecodeJob transformEncodeAndTranscode transcode后:" + transformed);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Transcoded transformed from source", startTime);
        }
        return result;
    }

    private void writeTransformedToCache(Resource<T> transformed) {
        Log.e("TAG",
                "DecodeJob writeTransformedToCache 是否可以缓存reuslt:" +
                        diskCacheStrategy.cacheResult());
        if (transformed == null || !diskCacheStrategy.cacheResult()) {
            return;
        }
        long startTime = LogTime.getLogTime();
        Log.e("TAG", "DecodeJob writeTransformedToCache 准备缓存调的是getEncoder  :");
        //调用的同样是DiskLruCache实例的put()方法，不过这里用的缓存Key是resultKey。
        SourceWriter<Resource<T>> writer =
                new SourceWriter<Resource<T>>(loadProvider.getEncoder(), transformed);
        diskCacheProvider.getDiskCache().put(resultKey, writer);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Wrote transformed from source to cache", startTime);
        }
    }

    // A:ImageVideoWrapper T:GifBitmapWrapper Z:GlideDrawable
    private Resource<T> decodeSource() throws Exception {
        Resource<T> decoded = null;
        try {
            long startTime = LogTime.getLogTime();
            // fetcher:ImageVideoFetcher    调用ImageVideoFetcher的loadData()方法
            //得到了一个ImageVideoWrapper对象
            //ImageVideoFetcher最终会对HttpUrlFetcher返回的Stream进行包装
            final A data = fetcher.loadData(priority);
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                logWithTimeAndKey("Fetched data", startTime);
            }
            if (isCancelled) {
                return null;
            }
            //如果允许缓存原图，会在该方法内处理
            decoded = decodeFromSourceData(data);
        } finally {
            fetcher.cleanup();
        }
        return decoded;
    }

    //A:ImageVideoWrapper T:GifBitmapWrapper Z:GlideDrawable
    private Resource<T> decodeFromSourceData(A data) throws IOException {
        final Resource<T> decoded;
        Log.e("TAG",
                "DecodeJob decodeFromSourceData 是否支持缓存结果diskCacheStrategy.cacheSource():" +
                        diskCacheStrategy.cacheSource());
        if (diskCacheStrategy.cacheSource()) {
            //先判断是否允许缓存原始图片，如果允许的话又会调用cacheAndDecodeSourceData()方法
            Log.e("TAG", "！！！！！！！！！！！！！DecodeJob decodeFromSourceData 缓存原始数据嘿嘿嘿:");
            decoded = cacheAndDecodeSourceData(data);
        } else {
            long startTime = LogTime.getLogTime();
            //loadProvider:FixedLoadProvider
            // loadProvider.getSourceDecoder():GifBitmapWrapperResourceDecoder
            //data:ImageVideoWrapper
            //这里需要主要的是loadProvider的方法，这调用的是getSourceDecoder，把ImageVideoWrapper里面的stream解码成drawable
            //loadProvider最终用的到的是ImageVideoGifDrawableLoadProvider（ImageVideoWrapper，GifBitmapWrapper）
            //注意：ResourceDecoder的第一个泛型参数作为输入参数，第二个泛型参数作为返回参数
            decoded = loadProvider.getSourceDecoder().decode(data, width, height);
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                logWithTimeAndKey("Decoded from source", startTime);
            }
        }
        return decoded;
    }

    private Resource<T> cacheAndDecodeSourceData(A data) throws IOException {
        long startTime = LogTime.getLogTime();
        SourceWriter<A> writer = new SourceWriter<A>(loadProvider.getSourceEncoder(), data);
        //同样调用了getDiskCache()方法来获取DiskLruCache实例
//        接着调用它的put()方法就可以写入硬盘缓存了(原始图片)
        //转换过后的图片缓存是在transformEncodeAndTranscode方法
        Log.d("TAG", "注意哦DecodeJob cacheAndDecodeSourceData 准备缓存source :" + resultKey.getId());
        diskCacheProvider.getDiskCache().put(resultKey.getOriginalKey(), writer);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            logWithTimeAndKey("Wrote source to cache", startTime);
        }

        startTime = LogTime.getLogTime();
        Log.e("TAG", "DecodeJob cacheAndDecodeSourceData 准备调用loadFromCache，传入的是orginalKey:" +
                resultKey.getOriginalKey());
        Resource<T> result = loadFromCache(resultKey.getOriginalKey());
        Log.w("TAG", "DecodeJob cacheAndDecodeSourceData 奇葩哦，又从内存:");
        if (Log.isLoggable(TAG, Log.VERBOSE) && result != null) {
            logWithTimeAndKey("Decoded source from cache", startTime);
        }
        return result;
    }

    //这个方法要注意，返回的是resource，传入的是key,T:GifBitmapWrapper
    private Resource<T> loadFromCache(Key key) throws IOException {
        //调用getDiskCache()方法获取到的就是Glide自己编写的DiskLruCache工具类的实例，然后调用它的get()方法并把缓存Key传入，就能得到硬盘缓存的文件了
        File cacheFile = diskCacheProvider.getDiskCache().get(key);
        if (cacheFile == null) {
            return null;
        }

        Resource<T> result = null;
        try {
            //如果文件不为空则将它解码成Resource对象后返回即可。FileToStreamDecoder
            //从文件到bitmap，相当于网络到bitmap，到时先转成inputStream，再转成bitmap
            //loadProvider:ImageVideoGifDrawableLoadProvider,最终嗲用到的是StreamBitmapDecoder
            //loadProvider.getCacheDecoder()->ImageVideoGifDrawableLoadProvider
            // .getCacheDecoder->FileToStreamDecoder->FileToStreamDecoder
            // 持有GifBitmapWrapperStreamResourceDecoder
            // ，最后调用的是持有GifBitmapWrapperStreamResourceDecoder.decode
            result = loadProvider.getCacheDecoder().decode(cacheFile, width, height);
            Log.w("TAG",
                    "缓存DecodeJob loadFromCache 调用loadProvider.getCacheDecoder() 解码后result:" + result);
        } finally {
            if (result == null) {
                diskCacheProvider.getDiskCache().delete(key);
            }
        }
        return result;
    }

    private Resource<T> transform(Resource<T> decoded) {
        if (decoded == null) {
            return null;
        }
        //transformation 做变换用的，如CenterCrop
        Resource<T> transformed = transformation.transform(decoded, width, height);
        if (!decoded.equals(transformed)) {
            decoded.recycle();
        }
        return transformed;
    }

    private Resource<Z> transcode(Resource<T> transformed) {
        if (transformed == null) {
            return null;
        }
        //        又是调用了transcoder的transcode()方法
//        transcoder:GifBitmapWrapperDrawableTranscoder
        return transcoder.transcode(transformed);
    }

    private void logWithTimeAndKey(String message, long startTime) {
        Log.v(TAG, message + " in " + LogTime.getElapsedMillis(startTime) + ", key: " + resultKey);
    }

    class SourceWriter<DataType> implements DiskCache.Writer {

        private final Encoder<DataType> encoder;
        private final DataType data;

        //encoder看这里，一个是source的，一个是result的
        //result的是resource<T>
        public SourceWriter(Encoder<DataType> encoder, DataType data) {
            this.encoder = encoder;
            this.data = data;
        }

        @Override
        public boolean write(File file) {
            boolean success = false;
            OutputStream os = null;
            try {
                //根据file打开输出流
                os = fileOpener.open(file);
                Log.e("TAG",
                        "SourceWriter 缓存 write 编码啦写入硬盘-----------------------------------:" +
                                file.getAbsolutePath());
                success = encoder.encode(data, os);
            } catch (FileNotFoundException e) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Failed to find file to write to disk cache", e);
                }
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        // Do nothing.
                    }
                }
            }
            return success;
        }
    }

    interface DiskCacheProvider {
        DiskCache getDiskCache();
    }

    static class FileOpener {
        public OutputStream open(File file) throws FileNotFoundException {
            return new BufferedOutputStream(new FileOutputStream(file));
        }
    }
}
