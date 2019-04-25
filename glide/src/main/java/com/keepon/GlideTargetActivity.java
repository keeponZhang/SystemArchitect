package com.keepon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.darren.architect_day03.R;
import com.keepon.target.CustomView;
import com.keepon.target.MyViewTarget;

public class GlideTargetActivity extends AppCompatActivity {
	String bookUrl = "http://ep.dzb.ciwong.com/rep/new/4055.jpg";
	private ImageView mImageView,mImageView2;

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
