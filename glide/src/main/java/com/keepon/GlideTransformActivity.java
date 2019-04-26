package com.keepon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.darren.architect_day03.R;
import com.keepon.transform.CircleCrop;
import com.keepon.transform.MyBitmapTransformation;

public class GlideTransformActivity extends AppCompatActivity {
	String bookUrl = "http://ep.dzb.ciwong.com/rep/new/4055.jpg";
	private ImageView mImageView;
	private static final String TAG = "GlideTransformActivity";
	private String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glide_transform);
		mImageView = findViewById(R.id.iv);
		Log.e(TAG, "imageView scaleType is " + mImageView.getScaleType());
	}
	public void loadTransform(View view) {
		MyBitmapTransformation myBitmapTransformation = new MyBitmapTransformation(this);
		Glide.with(this)
				.load(bookUrl)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.transform(myBitmapTransformation)
				.into(mImageView);
	}
//	也就是说，我们需要在load()方法中传入这个自定义的MyGlideUrl对象，而不能再像之前那样直接传入url字符串了。不然的话Glide在内部还是会使用原始的GlideUrl类，而不是我们自定义的MyGlideUrl类。
	public void loadMyGlideUrl(View view) {
		Glide.with(this)
				.load(new MyGlideUrl(bookUrl))
				.into(mImageView);
	}

	public void loadTransform2(View view) {
		mUrl = "https://www.baidu.com/img/bd_logo1.png";
		Glide.with(this)
				.load(mUrl)
				//dontTransform就不会全屏
				//使用dontTransform()方法存在着一个问题，就是调用这个方法之后，所有的图片变换操作就全部失效了
				.dontTransform()
				.into(mImageView);
//		Glide.with(this)
//				.load(url)
//				.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//				.into(mImageView);
	}
	String url2 = "http://cn.bing.com/az/hprichbg/rb/AvalancheCreek_ROW11173354624_1920x1080.jpg";

	public void loadCenterCropTransform2(View view) {
		//	centerCrop()方法还可以配合override()方法来实现更加丰富的效果，比如指定图片裁剪的比例
		Glide.with(this)
				.load(url2)
				.override(500, 500)
				.centerCrop()
				.into(mImageView);
	}
	public void loadCenterCropTransform(View view) {
		Glide.with(this)
				.load(url2)
				.centerCrop()
				.into(mImageView);
	}

	public void loadFitCenterTransform(View view) {
		Glide.with(this)
				.load(url2)
				//FitCenter的效果其实刚才我们已经见识过了，就是会将图片按照原始的长宽比充满全屏
				.fitCenter()
				.into(mImageView);
	}

	public void loadCircleTransform(View view) {
		Glide.with(this)
				.load(url2)
				.transform(new CircleCrop(this))
				.into(mImageView);
	}

	public void loadGlideTransforms(View view) {
//		blurTransformation();
	}

	private void blurTransformation() {
	/*	Glide.with(this)
				.load(url2)
				.bitmapTransform(new BlurTransformation(this))
				.into(mImageView);*/
	}
	private void grayscaleTransformation() {
	/*	Glide.with(this)
				.load(url2)
				.bitmapTransform(new GrayscaleTransformation(this))
//				.bitmapTransform(new BlurTransformation(this), new GrayscaleTransformation(this))
				.into(mImageView);*/
	}
}
