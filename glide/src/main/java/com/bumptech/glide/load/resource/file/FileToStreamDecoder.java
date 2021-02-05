package com.bumptech.glide.load.resource.file;

import android.util.Log;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A decoder that wraps an {@link InputStream} decoder to allow it to decode from a file.
 *
 * @param <T> The type of resource that the wrapped InputStream decoder decodes.
 */
public class FileToStreamDecoder<T> implements ResourceDecoder<File, T> {
    private static final FileOpener DEFAULT_FILE_OPENER = new FileOpener();

    private ResourceDecoder<InputStream, T> streamDecoder;
    private final FileOpener fileOpener;

    //接收这个表示下面会先把file先转换成InputStream
    public FileToStreamDecoder(ResourceDecoder<InputStream, T> streamDecoder) {
        this(streamDecoder, DEFAULT_FILE_OPENER);
    }

    // Exposed for testing.
    FileToStreamDecoder(ResourceDecoder<InputStream, T> streamDecoder, FileOpener fileOpener) {
        this.streamDecoder = streamDecoder;
        this.fileOpener = fileOpener;
    }

    @Override
    public Resource<T> decode(File source, int width, int height) throws IOException {
        InputStream is = null;
        Resource<T> result = null;
        try {
            is = fileOpener.open(source);
            //GifBitmapWrapperStreamResourceDecoder
            Log.e("TAG", "Decoder FileToStreamDecoder decode 这里其实走了个代理 从file streamDecoder:"+streamDecoder);
            result = streamDecoder.decode(is, width, height);
            Log.e("TAG", "DecoderFileToStreamDecoder decode 代理走完了 从file到 result:"+result);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }
        return result;
    }

    @Override
    public String getId() {
        return "";
    }

    // Visible for testing.
    static class FileOpener {
        public InputStream open(File file) throws FileNotFoundException {
            return new FileInputStream(file);
        }
    }
}
