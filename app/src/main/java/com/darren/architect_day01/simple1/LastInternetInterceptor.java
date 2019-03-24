package com.darren.architect_day01.simple1;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * @创建者 keepon
 * @创建时间 2019/3/11 0011 上午 9:54
 * @描述 ${TODO}
 * @版本 $$Rev$$
 * @更新者 $$Author$$
 * @更新时间 $$Date$$
 */
public class LastInternetInterceptor implements Interceptor {
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Response response = chain.proceed(request);
		Response responseLatest;




		if (NetworkUtils.isOnline()) {
//			int maxAge =  5;
//			Log.e(TAG, "intercept: maxAge  "+NetworkUtils.isOnline());
//			responseLatest = setCacheTime(response, maxAge);
			responseLatest = response;
		} else {
			int maxStale = 40; // 没网失效6小时
			Log.e(TAG, "intercept: maxStale "+NetworkUtils.isOnline());
			responseLatest = setNoNetWorkCacheTime(response, maxStale);
		}


		return responseLatest;
	}
	private static Response setCacheTime(Response response, int maxAge) {
		return response.newBuilder()
				.removeHeader("Pragma")
				.removeHeader("Cache-Control")
				.header("Cache-Control", "public, max-age=" + maxAge)
//				.header("Cache-Control", "private,max-age=20")
//				.header("Cache-Control", "no-store" )
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
