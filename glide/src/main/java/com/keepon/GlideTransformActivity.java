package com.keepon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.darren.architect_day03.R;
import com.keepon.transform.MyBitmapTransformation;

public class GlideTransformActivity extends AppCompatActivity {
	String bookUrl = "http://ep.dzb.ciwong.com/rep/new/4055.jpg";
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glide_transform);
		mImageView = findViewById(R.id.iv);
	}
	public void loadTransform(View view) {
		MyBitmapTransformation myBitmapTransformation = new MyBitmapTransformation(this);
		Glide.with(this)
				.load(bookUrl)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.transform(myBitmapTransformation)
				.into(mImageView);
	}
}
