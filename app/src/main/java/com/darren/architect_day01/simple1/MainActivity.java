package com.darren.architect_day01.simple1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.darren.architect_day01.BaseApplication;
import com.darren.architect_day01.R;

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

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
     int   cacheSize = 100 * 1024 * 1024;
    //分别对应缓存的目录，以及缓存的大小。
    Cache mCache = new Cache(BaseApplication.mApplicationContext.getCacheDir(), cacheSize);
    //在构造 OkHttpClient 时，通过 .cache 配置。
    OkHttpClient mOkHttpClient = new OkHttpClient.Builder().cache(mCache)
            .addInterceptor(new FirstClientInterceptor())
            .addNetworkInterceptor(new LastInternetInterceptor())
            .build();
    String testUrl = "";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv);
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
                        Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resultJson = response.body().string();
                Log.e("TAG", resultJson);
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("");
                        mTextView.setText(new Date().getTime()+" :  code == "+response.code()+"  "+resultJson);
                    }
                });
                // 1.JSON解析转换
                // 2.显示列表数据
                // 3.缓存数据
            }
        });
    }

    public void getPDF(View view) {
        testUrl = "http://edu100hqvideo.bs2cdn.100.com/Reise%E6%99%BA%E8%83%BD%E5%8C%96%E4%B8%BB%E8%B7%AF%E5%BE%84%E5%AD%A6%E4%B9%A0%E8%B5%84%E6%96%9901_d98bd461d9d724f551bce0aa7772dade5b96f0c5.pdf";
        download(testUrl);
    }
    public void getApk(View view) {
        testUrl = "http://repo.yypm.com/dwbuild/mobile/android/hqwx/5.3.0_maint/20190803-2486-r898e6e18652cc1de8ca5f1cfc7beb95fd32e5eaf/hqwx.apk";
        download(testUrl);
    }

    private void download(String url) {
        Request.Builder requestBuilder = new Request.Builder().url(url).tag(this);
        //可以省略，默认是GET请求
        final Request request = requestBuilder.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 失败
                Log.e("TAG", "MainActivity onFailure:");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = request.headers();
                    if(headers!=null){
                        String s = headers.toString();
                        Log.e("TAG", "MainActivity onResponse headers:"+s);
                    }

                    InputStream is = null;
                    FileOutputStream fos = null;
                    is = response.body().byteStream();
                    String fileName = Md5.strMd5(testUrl);
                    String path = getApplicationContext().getExternalCacheDir().getAbsolutePath();
                    File fileDir = new File(path, "materialdownload");
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    boolean isDownloadSuccess = false;
                    File file = new File(fileDir, fileName);
                    try {

                        byte[] bytes = new byte[4096];
                        //获取下载的文件的大小
                        long fileSize = response.body().contentLength();
                        if (file.exists() && file.length() == fileSize) {
                            Log.e("TAG", "MainActivity onResponse file.exists():");
                            isDownloadSuccess = true;
                        } else {
                            if (file.exists()) {
                                file.delete();
                            }
                            fos = new FileOutputStream(file);
                            int len = 0;
                            while ((len = is.read(bytes)) != -1) {
                                fos.write(bytes, 0, len);
                            }
                            fos.flush();
                            isDownloadSuccess = true;

                        }
                        Log.e("TAG", "MainActivity onResponse:" + isDownloadSuccess);
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
            }});
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
            }catch (Exception e){
                Log.e(TAG, "intercept: Exception");
            }


            Response response = chain.proceed(request);
            Response responseLatest;




                if (NetworkUtils.isOnline()) {
                    int maxAge =  60*5;
                    Log.e(TAG, "intercept: maxAge  "+NetworkUtils.isOnline());
                    responseLatest = setCacheTime(response, maxAge);
                } else {
                    int maxStale = 60 * 60 * 6; // 没网失效6小时
                    Log.e(TAG, "intercept: maxStale "+NetworkUtils.isOnline());
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
}
