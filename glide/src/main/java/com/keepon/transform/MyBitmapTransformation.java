package com.keepon.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class MyBitmapTransformation extends BitmapTransformation {

	public MyBitmapTransformation(Context context) {
		super(context);
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		Canvas canvas = new Canvas(toTransform);
		BitmapShader bitmapShader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		int min = Math.min(toTransform.getWidth(), toTransform.getHeight());
		int radius = min / 2;
		RadialGradient radialGradient = new RadialGradient(toTransform.getWidth() / 2, toTransform.getHeight() / 2, radius, Color.TRANSPARENT, Color.WHITE, Shader.TileMode.CLAMP);
		ComposeShader composeShader = new ComposeShader(bitmapShader, radialGradient, PorterDuff.Mode.SRC_OVER);
		Paint paint = new Paint();
		paint.setShader(composeShader);
		canvas.drawRect(0, 0, toTransform.getWidth(), toTransform.getHeight(), paint);
		return toTransform;
	}

	@Override
	public String getId() {
		return "MyBitmapTransformation";
	}
}
