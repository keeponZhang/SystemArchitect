package com.darren.architect_day24;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyActivity extends AppCompatActivity {

	private RequestQueue mQueue;
	private ImageView mImageView;
	private NetworkImageView mNetworkImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mQueue = Volley.newRequestQueue(this);
		mImageView = findViewById(R.id.iv);
		mNetworkImageView = (NetworkImageView) findViewById(R.id.network_image_view);

	}

	private static final String TAG = "VolleyActivity";
	public void stringRequest(View view) {
		String url ="https://www.baidu.com";
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				Log.e(TAG, "stringRequest onResponse: "+s );
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.e(TAG, "stringRequest onErrorResponse: " );
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("params1", "value1");
				map.put("params2", "value2");
				return map;
			}
		};
		mQueue.add(stringRequest);
	}

	public void jsonRequest(View view) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html", null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, "JsonObjectRequest onResponse:"+response.toString());
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse="+error.getMessage(), error);
			}
		});
		mQueue.add(jsonObjectRequest);
	}

	public void imageRequest(View view) {

		ImageRequest imageRequest = new ImageRequest(
				"http://ep.dzb.ciwong.com/rep/3159ba11c79d016ff8b9ea82a84ed962.jpg",
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						mImageView.setImageBitmap(response);
					}
				}, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mImageView.setImageResource(R.mipmap.ic_launcher);
			}
		});
		mQueue.add(imageRequest);
	}

	public void imageLoader(View view) {
		ImageLoader imageLoader1 = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
			}

			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}
		});
		ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView,
				R.mipmap.ic_launcher, R.mipmap.ic_launcher);
		imageLoader.get("https://img-my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", listener);

	}

	public void networkImageView(View view) {
		ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
		mNetworkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
		mNetworkImageView.setErrorImageResId(R.mipmap.ic_launcher);
		mNetworkImageView.setImageUrl("https://img-my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
				imageLoader);
	}

	public void gsonRequest(View view) {
	}

	public void xmlRequest(View view) {
		XMLRequest xmlRequest = new XMLRequest(
				"http://flash.weather.com.cn/wmaps/xml/china.xml",
				new Response.Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						try {
							int eventType = response.getEventType();
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {
									case XmlPullParser.START_TAG:
										String nodeName = response.getName();
										if ("city".equals(nodeName)) {
											String pName = response.getAttributeValue(0);
											Log.e(TAG, "xmlRequest onResponse pName is " + pName);
										}
										break;
								}
								eventType = response.next();
							}
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "xmlRequest onErrorResponse:"+error.getMessage(), error);
			}
		});
		mQueue.add(xmlRequest);
	}
}