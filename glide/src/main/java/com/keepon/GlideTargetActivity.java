package com.keepon;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.darren.architect_day03.R;
import com.keepon.target.CustomView;
import com.keepon.target.DownloadImageTarget;
import com.keepon.target.MyViewTarget;

import java.io.File;

public class GlideTargetActivity extends AppCompatActivity {
	String bookUrl = "http://ep.dzb.ciwong.com/rep/new/4055.jpg";
	private ImageView mImageView,mImageView2;
//	通常只需要在两种Target的基础上去自定义就可以了，一种是SimpleTarget，一种是ViewTarget
//	Glide在内部自动帮我们创建的GlideDrawableImageViewTarget就是ViewTarget的子类
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glide_target);
		mImageView = findViewById(R.id.iv);
	}
	public void loadSimpleTarget(View view) {
		MySimpleTarget mySimpleTarget = new MySimpleTarget();
		Glide.with(this)
				.load(bookUrl)
				.into(mySimpleTarget);
	}
	public void loadSimpleTarget2(View view) {
		MySimpleTarget mySimpleTarget = new MySimpleTarget(50, 50);
		Glide.with(this)
				.load(bookUrl)
				.into(mySimpleTarget);
	}

	public void preload(View view) {
		Glide.with(this)
				.load(bookUrl)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
//		preload()方法有两个方法重载，一个不带参数，表示将会加载图片的原始尺寸，另一个可以通过参数指定加载图片的宽和高。
				//会使用PreloadTarget
				.preload();
	}
	//					downloadOnly(int width, int height)
	//					downloadOnly(Y target)
	public void downloadOnly(View view) {
//		其实downloadOnly(int width, int height)方法必须使用在子线程当中，
//		最主要还是因为它在内部帮我们自动创建了一个RequestFutureTarget，
//		是这个RequestFutureTarget要求必须在子线程当中执行。
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";
					final Context context = getApplicationContext();

					//target:RequestFutureTarget
					FutureTarget<File> target = Glide.with(context)
							.load(url)
							.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
					final File imageFile = target.get();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, imageFile.getPath(), Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void loadDownloadOnly(View view) {
		//需要注意的是，这里必须将硬盘缓存策略指定成DiskCacheStrategy.SOURCE或者DiskCacheStrategy.ALL，否则Glide将无法使用我们刚才下载好的图片缓存文件。
		String url = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";
		Glide.with(this)
				.load(url)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.into(mImageView);

	}

	public void loadDownloadOnly2(View view) {
		String url = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";
		Glide.with(this)
				.load(url)
				.downloadOnly(new DownloadImageTarget());
	}

	public void listener(View view) {
//		listener()方法是定义在GenericRequestBuilder类当中的，而我们传入到listener()方法中的实例则会赋值到一个requestListener变量当中
		String url = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";
		Glide.with(this)
				.load(url)
				.listener(new RequestListener<String, GlideDrawable>() {
					@Override
					public boolean onException(Exception e, String model, Target<GlideDrawable> target,
					                           boolean isFirstResource) {
						return false;
					}
					//		如果我们在RequestListener的onResourceReady()方法中返回了true，那么就不会再回调Target的onResourceReady()方法了
					@Override
					public boolean onResourceReady(GlideDrawable resource, String model,
					                               Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
						return false;
					}
				})
				.into(mImageView);

	}


	private class MySimpleTarget extends SimpleTarget<GlideDrawable> {
		public MySimpleTarget(){
			super();
		}

		public MySimpleTarget(int width, int height) {
			super(width, height);
		}
		@Override
		public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
			mImageView.setImageDrawable(resource.getCurrent());
		}
	}


	public void loadViewTarget(View view) {
		CustomView customView = (CustomView) findViewById(R.id.cv_result);
		MyViewTarget myViewTarget = new MyViewTarget(customView);
		Glide.with(this)
				.load(bookUrl)
				.into(myViewTarget);
	}
}
