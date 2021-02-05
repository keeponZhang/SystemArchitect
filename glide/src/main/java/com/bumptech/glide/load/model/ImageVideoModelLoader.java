package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.InputStream;

/**
 * A wrapper model loader that provides both an {@link InputStream} and a
 * {@link ParcelFileDescriptor} for a given model type by wrapping an
 * {@link ModelLoader} for {@link InputStream}s for the given model type and an
 * {@link ModelLoader} for {@link ParcelFileDescriptor} for the given model
 * type.
 *
 * @param <A> The model type.
 */
//A:String
public class ImageVideoModelLoader<A> implements ModelLoader<A, ImageVideoWrapper> {
    private static final String TAG = "IVML";

    //StreamStringLoader
    private final ModelLoader<A, InputStream> streamLoader;
    //FileDescriptorStringLoader
    private final ModelLoader<A, ParcelFileDescriptor> fileDescriptorLoader;

    //可以说是代理
    public ImageVideoModelLoader(ModelLoader<A, InputStream> streamLoader,
            ModelLoader<A, ParcelFileDescriptor> fileDescriptorLoader) {
        if (streamLoader == null && fileDescriptorLoader == null) {
            throw new NullPointerException("At least one of streamLoader and fileDescriptorLoader must be non null");
        }
        // streamLoader:StreamStringLoader
        this.streamLoader = streamLoader;
        //streamLoader： FileDescriptorStringLoader
        this.fileDescriptorLoader = fileDescriptorLoader;
    }

    @Override
    public DataFetcher<ImageVideoWrapper> getResourceFetcher(A model, int width, int height) {
        DataFetcher<InputStream> streamFetcher = null;
        if (streamLoader != null) {
            //streamLoader.getResourceFetcher()方法获取一个DataFetcher
            //streamLoader:StreamUriLoader
            //streamFetcher:HttpUrlFetcher
            streamFetcher = streamLoader.getResourceFetcher(model, width, height);
        }
        DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher = null;
        if (fileDescriptorLoader != null) {
            fileDescriptorFetcher = fileDescriptorLoader.getResourceFetcher(model, width, height);
            Log.w("TAG", "ImageVideoModelLoader getResourceFetcher 返回:"+fileDescriptorFetcher);
        }

        if (streamFetcher != null || fileDescriptorFetcher != null) {
            return new ImageVideoFetcher(streamFetcher, fileDescriptorFetcher);
        } else {
            return null;
        }
    }

    static class ImageVideoFetcher implements DataFetcher<ImageVideoWrapper> {
        //streamFetcher:HttpUrlFetcher
        private final DataFetcher<InputStream> streamFetcher;
        private final DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher;

        //streamFetcher:HttpUrlFetcher
        public ImageVideoFetcher(DataFetcher<InputStream> streamFetcher,
                DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher) {
            // streamFetcher：HttpUrlFetcher
            this.streamFetcher = streamFetcher;
            this.fileDescriptorFetcher = fileDescriptorFetcher;
        }

        @SuppressWarnings("resource")
        // @see ModelLoader.loadData
        @Override
        public ImageVideoWrapper loadData(Priority priority) throws Exception {
            InputStream is = null;
            if (streamFetcher != null) {
                try {
                    //又去调用了HttpUrlFetcher.loadData()方法，这里返回了网络流
                    Log.e("TAG", "ImageVideoFetcher loadData streamFetcher不为空:");
                    is = streamFetcher.loadData(priority);
                } catch (Exception e) {
                    if (Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.v(TAG, "Exception fetching input stream, trying ParcelFileDescriptor", e);
                    }
                    if (fileDescriptorFetcher == null) {
                        throw e;
                    }
                }
            }
            ParcelFileDescriptor fileDescriptor = null;
            if (fileDescriptorFetcher != null) {
                Log.e("TAG", "ImageVideoFetcher loadData fileDescriptorFetcher不为空: ");
                try {
                    fileDescriptor = fileDescriptorFetcher.loadData(priority);
                } catch (Exception e) {
                    if (Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.v(TAG, "Exception fetching ParcelFileDescriptor", e);
                    }
                    if (is == null) {
                        throw e;
                    }
                }
            }
//            创建了一个ImageVideoWrapper对象
            return new ImageVideoWrapper(is, fileDescriptor);
        }

        @Override
        public void cleanup() {
            //TODO: what if this throws?
            if (streamFetcher != null) {
                streamFetcher.cleanup();
            }
            if (fileDescriptorFetcher != null) {
                fileDescriptorFetcher.cleanup();
            }
        }

        @Override
        public String getId() {
            // Both the stream fetcher and the file descriptor fetcher should return the same id.
            if (streamFetcher != null) {
                return streamFetcher.getId();
            } else {
                return fileDescriptorFetcher.getId();
            }
        }

        @Override
        public void cancel() {
            if (streamFetcher != null) {
                streamFetcher.cancel();
            }
            if (fileDescriptorFetcher != null) {
                fileDescriptorFetcher.cancel();
            }
        }
    }
}
