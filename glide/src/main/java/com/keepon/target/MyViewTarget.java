package com.keepon.target;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

public class MyViewTarget extends ViewTarget<CustomView, GlideDrawable> {

        public MyViewTarget(CustomView customView) {
            super(customView);
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            view.setResult(resource.getCurrent());
        }
    }