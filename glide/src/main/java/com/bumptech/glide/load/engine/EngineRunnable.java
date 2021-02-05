package com.bumptech.glide.load.engine;

import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.executor.Prioritized;
import com.bumptech.glide.request.ResourceCallback;

/**
 * A runnable class responsible for using an {@link DecodeJob} to decode resources on a
 * background thread in two stages.
 *
 * <p>
 *     In the first stage, this class attempts to decode a resource
 *     from cache, first using transformed data and then using source data. If no resource can be decoded from cache,
 *     this class then requests to be posted again. During the second stage this class then attempts to use the
 *     {@link DecodeJob} to decode data directly from the original source.
 * </p>
 *
 * <p>
 *     Using two stages with a re-post in between allows us to run fast disk cache decodes on one thread and slow source
 *     fetches on a second pool so that loads for local data are never blocked waiting for loads for remote data to
 *     complete.
 * </p>
 */
class EngineRunnable implements Runnable, Prioritized {
    private static final String TAG = "EngineRunnable";

    private final Priority priority;
    private final EngineRunnableManager manager;
    private final DecodeJob<?, ?, ?> decodeJob;

    private Stage stage;

    private volatile boolean isCancelled;

    public EngineRunnable(EngineRunnableManager manager, DecodeJob<?, ?, ?> decodeJob, Priority priority) {
        this.manager = manager;
        this.decodeJob = decodeJob;
        this.stage = Stage.CACHE;
        this.priority = priority;
    }

    public void cancel() {
        isCancelled = true;
        decodeJob.cancel();
    }

    @Override
    public void run() {
        if (isCancelled) {
            return;
        }

        Exception exception = null;
        Resource<?> resource = null;
        try {
//            调用了一个decode()方法，并且这个方法返回了一个Resource对象
            //resource:Resource<GlideDrawable>对象
            // (通过GifBitmapWrapperDrawableTranscoder对GifBitmapWrapper转码得来的)
            Log.d("TAG", "EngineRunnable run 运行啦:");
            resource = decode();
        } catch (Exception e) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Exception decoding", e);
            }
            exception = e;
        }

        if (isCancelled) {
            if (resource != null) {
                resource.recycle();
            }
            return;
        }

        if (resource == null) {
            onLoadFailed(exception);
        } else {
            onLoadComplete(resource);
        }
    }

    private boolean isDecodingFromCache() {
        return stage == Stage.CACHE;
    }

    private void onLoadComplete(Resource resource) {
        //这个manager就是EngineJob对象，因此这里实际上调用的是EngineJob的onResourceReady()方法
        Log.e("TAG", "EngineRunnable onLoadComplete resource:"+resource);
        manager.onResourceReady(resource);
    }

    private void onLoadFailed(Exception e) {
        //从本地获取失败一次后才去获取网络
        if (isDecodingFromCache()) {
            stage = Stage.SOURCE;
            //这里才有开启真正去请求的runnable
            manager.submitForSource(this);
        } else {
            manager.onException(e);
        }
    }

    private Resource<?> decode() throws Exception {
        //从缓存当中去decode图片的话就会执行decodeFromCache()
        Log.w("TAG", "注意EngineRunnable decode isDecodingFromCache的值:"+isDecodingFromCache());
        if (isDecodingFromCache()) {
            //调用decodeFromCache()方法从硬盘缓存当中读取图片
            return decodeFromCache();
        } else {
            //否则的话就执行decodeFromSource()
            //再回到run()方法当中
          //在没有缓存的情况下，会调用decodeFromSource()方法来读取原始图片
            Log.e("TAG", "EngineRunnable 木有缓存decodeFromSource:");
            return decodeFromSource();
        }
    }

    private Resource<?> decodeFromCache() throws Exception {
        Resource<?> result = null;
        try {
            //先去调用DecodeJob的decodeResultFromCache()方法来获取缓存
            //如果是decodeResultFromCache()方法就直接将数据解码并返回
            result = decodeJob.decodeResultFromCache();
            Log.e("TAG", "EngineRunnable decodeFromCache result:"+result);
        } catch (Exception e) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Exception decoding result from cache: " + e);
            }
        }

        if (result == null) {
            //如果获取不到，会再调用decodeSourceFromCache()方法获取缓存
            //这两个方法的区别其实就是DiskCacheStrategy.RESULT和DiskCacheStrategy.SOURCE这两个参数的区别
            ////        如果是decodeSourceFromCache()方法，还要调用一下transformEncodeAndTranscode()方法先将数据转换一下再解码并返回
            result = decodeJob.decodeSourceFromCache();
        }
        return result;
    }

    private Resource<?> decodeFromSource() throws Exception {
        //这里又调用了DecodeJob的decodeFromSource()方法。刚才已经说了，DecodeJob的任务十分繁重，我们继续跟进看一看吧
        //所以EngineRunable只是起了个子线程的作用，真正的还是DecodeJob
        return decodeJob.decodeFromSource();
    }

    @Override
    public int getPriority() {
        return priority.ordinal();
    }

    private enum Stage {
        /** Attempting to decode resource from cache. */
        CACHE,
        /** Attempting to decode resource from source data. */
        SOURCE
    }

    interface EngineRunnableManager extends ResourceCallback {
        void submitForSource(EngineRunnable runnable);
    }
}
