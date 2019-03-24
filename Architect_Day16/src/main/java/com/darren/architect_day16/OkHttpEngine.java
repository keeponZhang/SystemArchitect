package com.darren.architect_day16;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.gohttp.IHttpEngine;
import com.gohttp.approve.Utils;
import com.gohttp.callback.EngineCallback;
import com.gohttp.callback.EngineDownloadCallback;
import com.gohttp.callback.EngineUploadCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Email 240336124@qq.com
 * Created by Darren on 2017/3/4.
 * Version 1.0
 * Description: OkHttp默认的引擎
 */
public class OkHttpEngine implements IHttpEngine {
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    private static Handler mHandler = new Handler();

    @Override
    public void post(final boolean cache, Context context, String url, Map<String, Object> params, final EngineCallback callBack) {

        final String finalUrl = Utils.jointParams(url, params);  //打印
        Log.e("Post请求路径：", finalUrl);


        // 了解 Okhhtp
        RequestBody requestBody = appendBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(
                new Callback() {

                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 这个 两个回掉方法都不是在主线程中
                        final String resultJson = response.body().string();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(resultJson);
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void download(String url, final EngineDownloadCallback downLoadCallBack) {
        Log.e("下载路径：", url);
        Request request = new Request.Builder().url(url).build();

        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                downLoadCallBack.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 这个 两个回掉方法都不是在主线程中
                        final InputStream is = response.body().byteStream();
                        final long total = response.body().contentLength();
                        downLoadCallBack.onResponse(is, total);
                    }
                }
        );
    }

    @Override
    public void upload(Context context, String url, Map<String, Object> params, final EngineUploadCallback callBack) {
        final String jointUrl = Utils.jointParams(url, params);  //打印
        Log.e("上传路径：", jointUrl);

        // 了解 Okhhtp
        RequestBody requestBody = appendBody(params);

        // 扩展一个上传的进度条
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                /*.post(new ProgressRequestBody(requestBody, new ProgressRequestListener() {
                    @Override
                    public void onRequestProgress(final long bytesWritten, final long contentLength, boolean done) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onResponse(bytesWritten, contentLength);
                                if (bytesWritten / contentLength == 1) {
                                    callBack.onComplete();
                                }
                            }
                        });
                   }
                }))*/.build();


//        OkHttpClient okHttpClient = new  OkHttpClient.Builder().connectTimeout(200, TimeUnit.SECONDS).build();
        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String resultJson = response.body().string();
                        Log.e("json->", resultJson);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(resultJson);
                            }
                        });
                    }
                }
        );
    }

    /**
     * 组装post请求参数body
     */
    protected RequestBody appendBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    // 添加参数
    private void addParams(MultipartBody.Builder builder, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                Object value = params.get(key);

                if (value instanceof File) {
                    // 处理文件 --> Object File
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody
                            .create(MediaType.parse(guessMimeType(file
                                    .getAbsolutePath())), file));
                } else if (value instanceof List) {
                    // 代表提交的是 List集合
                    try {
                        List<File> listFiles = (List<File>) value;
                        for (int i = 0; i < listFiles.size(); i++) {
                            // 获取文件
                            File file = listFiles.get(i);
                            builder.addFormDataPart(key + i, file.getName(), RequestBody
                                    .create(MediaType.parse(guessMimeType(file
                                            .getAbsolutePath())), file));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.addFormDataPart(key, value + "");
                }
            }
        }
    }

    /**
     * 猜测文件类型
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    @Override
    public void get(final boolean cache, Context context, String url, Map<String, Object> params, final EngineCallback callBack) {
        // 请求路径  参数 + 路径代表唯一标识
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue() + "");
        }

        HttpUrl httpUrl = httpUrlBuilder.build();

        final String finalUrl = httpUrl.toString();

        Log.e("Get请求路径：", finalUrl);


        Request.Builder requestBuilder = new Request.Builder().url(httpUrl).tag(context);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 都不是在主线程中
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resultJson = response.body().string();

                mHandler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      // 2.2 执行成功方法
                                      callBack.onSuccess(resultJson);
                                  }
                              }
                );

            }
        });
    }


    public static void cancel(Context tag) {

        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        synchronized (dispatcher) {
            for (Call call : dispatcher.queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : dispatcher.runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }

            }
        }
    }
}
