package com.keepon.target;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
//需要指定两个泛型，一个是View的类型，一个图片的类型（GlideDrawable或Bitmap）
public class MyViewTarget extends ViewTarget<CustomView, GlideDrawable> {

        public MyViewTarget(CustomView customView) {
            super(customView);
        }

        //GenericRequest的onResourceReady方法会调用viewTarget的onResourceReady方法
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            //通过getview方法可以拿到泛型中的view
            CustomView view = getView();
            this.view.setResult(resource.getCurrent());
        }
    }