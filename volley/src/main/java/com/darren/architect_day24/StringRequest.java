package com.darren.architect_day24;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class StringRequest extends Request<String> {
    private final Response.Listener<String> mListener;
 
    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Response.Listener<String> listener,
            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }
 
    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    //deliverResponse()方法中的实现很简单，仅仅是调用了mListener中的onResponse()方法
    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
//    parseNetworkResponse()方法中则应该对服务器响应的数据进行解析，
// 其中数据是以字节的形式存放在NetworkResponse的data变量中的，这里将数据取出然后组装成一个String，并传入Response的success()方法中即可。
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
