package com.darren.architect_day01.simple1;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darren.architect_day01.BaseApplication;
import com.darren.architect_day01.R;
import com.darren.architect_day01.data.repsonse.BaseRes;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * createBy keepon
 */
public class OkHttpSourceActivity extends AppCompatActivity {
    private static final String TAG = "OkHttpSourceActivity";
    String testUrl = "";
    private TextView mTextView;
    OkHttpClient mOkHttpClient;
    private ImageView mIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_okhttpsouce);
        mTextView = (TextView) findViewById(R.id.tv);
        mIv = (ImageView) findViewById(R.id.iv);
        init();

    }

    public void testBitmap(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cspro_icon_robot);
        mIv.setImageBitmap(bitmap);
        // if(bitmap!=null){
        //     bitmap.recycle();
        // }

    }

    public void init(View view) {
        init();
    }

    private void init() {
        int cacheSize = 5 * 1024 * 1024;
        //分别对应缓存的目录，以及缓存的大小。
        Cache mCache =
                new Cache(BaseApplication.mApplicationContext.getExternalCacheDir(), cacheSize);
        //在构造 OkHttpClient 时，通过 .cache 配置。
        mOkHttpClient = new OkHttpClient.Builder().cache(mCache)
                //使用缓存必须增加FirstClientInterceptor
                .addInterceptor(new FirstClientInterceptor())
                .addInterceptor(new MyCacheInterceptor())
                .addNetworkInterceptor(new LastInternetInterceptor())
                .build();
    }

    public void getAppXixiUpdate(View view) {
        testUrl = "http://eapi.ciwong.com/repos/launcher/android/update";
        Request.Builder requestBuilder = new Request.Builder().url(testUrl).tag(this);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();


        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OkHttpSourceActivity.this, "获取数据失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resultJson = response.body().string();
                Log.d("TAG", resultJson);
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("");
                        mTextView.setText(
                                new Date().getTime() + " :  code == " + response.code() + "  " +
                                        resultJson);
                    }
                });
                // 1.JSON解析转换
                // 2.显示列表数据
                // 3.缓存数据
            }
        });
    }

    public void getPDF(View view) {
        testUrl =
                "http://edu100hqvideo.bs2cdn.100.com/Reise%E6%99%BA%E8%83%BD%E5%8C%96%E4%B8%BB%E8%B7%AF%E5%BE%84%E5%AD%A6%E4%B9%A0%E8%B5%84%E6%96%9901_d98bd461d9d724f551bce0aa7772dade5b96f0c5.pdf";
        download(testUrl);
    }

    public void getApk(View view) {
//        testUrl = "http://repo.yypm.com/dwbuild/mobile/android/hqwx/5.3.0_maint/20190803-2486-r898e6e18652cc1de8ca5f1cfc7beb95fd32e5eaf/hqwx.apk";
        testUrl = "http://gyxza3.eymlz.com/yx1/yx_wwj6/youxiayingshi.apk";
        download(testUrl);
    }

    private void download(String url) {
        if (mOkHttpClient == null) {
            init(mTextView);
        }
        Request.Builder requestBuilder = new Request.Builder().url(url).tag(this);
        //可以省略，默认是GET请求
        final Request request = requestBuilder.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 失败
                Log.e("TAG", "OkHttpSourceActivity onFailure:");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = request.headers();
                    if (headers != null) {
                        String s = headers.toString();
                        Log.e("TAG", "OkHttpSourceActivity onResponse headers:" + s);
                    }

                    InputStream is = null;
                    FileOutputStream fos = null;
                    is = response.body().byteStream();
                    String fileName = Md5.strMd5(testUrl);
                    String path = getApplicationContext().getExternalCacheDir().getAbsolutePath();
                    File fileDir = new File(path);
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    File materialdownload = new File(path, "materialdownload");
                    if (!materialdownload.exists()) {
                        materialdownload.mkdir();
                    }
                    boolean isDownloadSuccess = false;
                    File file = new File(materialdownload, fileName);
                    try {

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OkHttpSourceActivity.this, "准备写入", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        byte[] bytes = new byte[4096];
                        //获取下载的文件的大小
                        long fileSize = response.body().contentLength();
                        if (file.exists() && file.length() == fileSize) {
                            Log.e("TAG", "OkHttpSourceActivity onResponse file.exists():");
                            isDownloadSuccess = true;
                        } else {
                            if (file.exists()) {
                                file.delete();
                            }
                            fos = new FileOutputStream(file);
                            int len = 0;
                            while ((len = is.read(bytes)) != -1) {
                                Log.d("TAG", "OkHttpSourceActivity onResponse fos.write len:" + len);
                                fos.write(bytes, 0, len);
                            }
                            fos.flush();
                            isDownloadSuccess = true;

                        }
                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OkHttpSourceActivity.this, "写入成功", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        Log.e("TAG", "OkHttpSourceActivity onResponse:" + isDownloadSuccess);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });
    }

    static Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            try {
                if (!NetworkUtils.isOnline()) {//没网强制从缓存读取
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                } else {
                    request = request.newBuilder().removeHeader("If-None-Match").build();
                }
            } catch (Exception e) {
                Log.e(TAG, "intercept: Exception");
            }


            Response response = chain.proceed(request);
            Response responseLatest;


            if (NetworkUtils.isOnline()) {
                int maxAge = 60 * 5;
                Log.e(TAG, "intercept: maxAge  " + NetworkUtils.isOnline());
                responseLatest = setCacheTime(response, maxAge);
            } else {
                int maxStale = 60 * 60 * 6; // 没网失效6小时
                Log.e(TAG, "intercept: maxStale " + NetworkUtils.isOnline());
                responseLatest = setNoNetWorkCacheTime(response, maxStale);
            }


            return responseLatest;


        }
    };

    private static Response setCacheTime(Response response, int maxAge) {
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();
    }

    private static Response setNoNetWorkCacheTime(Response response, int maxStale) {
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                .build();
    }

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists()
                && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public void clearCache(View view) {
        deleteFilesByDirectory(getCacheDir());
        deleteFilesByDirectory(getExternalCacheDir());
        Toast.makeText(OkHttpSourceActivity.this, "删除缓存成功!", Toast.LENGTH_LONG).show();
    }

    private void getUserAssessment(View view) {
        testUrl = "http://japi.hqwx.com/al/userAssessment/get";
        Request.Builder requestBuilder = new Request.Builder().url(testUrl).tag(this);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();


        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OkHttpSourceActivity.this, "获取数据失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resultJson = response.body().string();
                Gson gson = new Gson();
                BaseRes csProEvaluateRecordRes =
                        gson.fromJson(resultJson, BaseRes.class);
                Log.e("TAG", "OkHttpSourceActivity onResponse mStatus.code:" +
                        csProEvaluateRecordRes.mStatus.code);
            }
        });
    }


    public void jumpOtherApp(View view) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.zhang.jetpack", "com.zhang.jetpack" +
                    ".OkHttpSourceActivity"));
            // intent.setData(Uri.parse("www.keepon.com"));
            intent.setData(Uri.parse("http://www.keepon.com"));
            // intent.setAction()
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("TAG", "OkHttpSourceActivity jumpOtherApp falied:" );
        }
    }
}
