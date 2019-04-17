package com.darren.architect_day26.okhttp.interceptor;

import android.util.Log;

import com.darren.architect_day26.okhttp.Request;
import com.darren.architect_day26.okhttp.RequestBody;
import com.darren.architect_day26.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hcDarren on 2017/11/19.
 */

//真正的网络请求在这个拦截器
public class CallServerInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("TAG","CallServerInterceptor");
        Request request = chain.request();
        URL url = new URL(request.url());

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
            // https 的一些操作
            // httpsURLConnection.setHostnameVerifier();
            // httpsURLConnection.setSSLSocketFactory();
        }
        // urlConnection.setReadTimeout();

        // 没有做缓存 304 ，没有做重定向，没有特殊处理，没有连接复用

        // 写东西
        urlConnection.setRequestMethod(request.method.name);
        urlConnection.setDoOutput(request.method.doOutput());

        urlConnection.connect();
        // 写内容
        RequestBody requestBody = request.requestBody();
        if (requestBody != null) {
            requestBody.onWriteBody(urlConnection.getOutputStream());
        }

        int statusCode = urlConnection.getResponseCode();
        InputStream inputStream = urlConnection.getInputStream();
        Response response = new Response(inputStream);
        if (statusCode == 200) {
//            InputStream inputStream = urlConnection.getInputStream();
//            Response response = new Response(inputStream);
            return response;
        }
        // 进行一些列操作，状态码 200
        return null;
    }
}
