package com.keepon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.darren.architect_day03.R;
import com.keepon.util.DisplayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class GlideActivity extends AppCompatActivity {

	private ImageView mImageView,mImageView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glide);
		mImageView = findViewById(R.id.iv);
		mImageView2 = findViewById(R.id.iv2);
	}

	public void loadUrl(View view) {
		RelativeLayout.LayoutParams layoutParams =
				(RelativeLayout.LayoutParams) mImageView.getLayoutParams();
		// layoutParams.width = DisplayUtils.dip2px(100);
		// layoutParams.height = DisplayUtils.dip2px(100);
		// mImageView.setLayoutParams(layoutParams);
		Glide.with(GlideActivity.this)//传入关联的Context，如果是Activity/Fragment
				// ，那么它会根据组件当前的状态来控制请求。设置进去的sourceEncoder会覆盖
        .load("http://ep.dzb.ciwong.com/rep/new/4055.jpg").diskCacheStrategy(DiskCacheStrategy.ALL)
				//需要加载的图片，大多数情况下就是网络图片的链接。
		.into(mImageView); //用来展现图片的ImageView.
	}

	public void loadByteArray(View view) {
		Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.i04);
		ByteArrayOutputStream bArrayOS = new ByteArrayOutputStream();
		sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, bArrayOS);
		sourceBitmap.recycle();
		byte[] byteArray = bArrayOS.toByteArray();
		try {
			bArrayOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Glide.with(this)
				.load(byteArray)
				.into(mImageView);
	}

	public void loadFile(View view) {
		String storePath = "/sdcard/01.png";
		File file = new File(storePath);
		Glide.with(this)
				.load(file)
				.into(mImageView);
	}

	public void loadResourceId(View view) {
		Glide.with(this)
				.load(R.mipmap.ic_launcher)
				.into(mImageView);
	}

	public void loadGif(View view) {
		Glide.with(this)
				.load("http://s1.dwstatic.com/group1/M00/66/4D/d52ff9b0727dfd0133a52de627e39d2a.gif")
				.diskCacheStrategy(DiskCacheStrategy.SOURCE) //要加上这句，否则有可能会出现加载很慢，或者加载不出来的情况.
				.into(mImageView);
	}

	public void loadMedia(View view) {
		String storePath = "/sdcard/1.mp4";
		File file = new File(storePath);
		Glide.with(this)
				.load(Uri.fromFile(file))
				.into(mImageView);
	}

	public void loadHolder(View view) {
		Glide.with(this)
				.load("http://i.imgur.com/DvpvklR.png")
				.placeholder(R.mipmap.book_placeholder)
				.into(mImageView);
	}


	public void loadHolderError(View view) {
		Glide.with(this)
				.load("http://i.imgur.com/DvpvklR.png")
				.asGif()  //为了模拟加载失败的情况.
				.placeholder(R.mipmap.book_placeholder)
				.error(R.mipmap.error_img)
				.into(mImageView);
	}

	public void loadCustomCrossFade(View view) {
		Glide.with(this)
				.load("http://ep.dzb.ciwong.com/rep/image/3170.jpg")
				.placeholder(R.mipmap.book_placeholder)
				.crossFade(5000) //改变的时长.
				.into(mImageView);
	}

	public void loadNoCrossFade(View view) {
		Glide.with(this)
				.load("http://ep.dzb.ciwong.com/rep/image/3173.jpg")
				.placeholder(R.mipmap.book_placeholder)
				.dontAnimate()
				.into(mImageView);
	}




	public void loadOverride(View view) {
		Glide.with(this)
				.load("http://ep.dzb.ciwong.com/rep/image/3173.jpg")
			.override(200, 200)
				.into(mImageView2);
		Log.e(TAG, "loadOverride: "+mImageView2.getMeasuredHeight()+ " :" +mImageView2.getMeasuredHeight() );
	}

	private static final String TAG = "GlideActivity";
	public void loadOverrideCenterCrop(View view) {
		Glide.with(this)
//				.load("http://ep.dzb.ciwong.com/rep/image/3173.jpg")
				.load("http://ep.dzb.ciwong.com/rep/new/4055.jpg")
				//（override后不充满了）
//				.override(600, 600)
				.centerCrop()
				.into(mImageView2);
		Log.e(TAG, "loadOverrideCenterCrop: "+mImageView2.getMeasuredHeight()+ " :" +mImageView2.getMeasuredHeight() );
	}
	public void loadOverrideFitCenter(View view) {
		Glide.with(this)
				.load("http://ep.dzb.ciwong.com/rep/image/3173.jpg")
				.override(200, 200)
				.fitCenter()
				.into(mImageView2);
		Log.e(TAG, "loadOverrideFitCenter: "+mImageView2.getMeasuredHeight()+ " :" +mImageView2.getMeasuredHeight() );

	}

	public void testLruCache(View view) {
		LruCacheSamples.startRun();
	}
}
