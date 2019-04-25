package com.keepon.animate;

import android.animation.ValueAnimator;
import android.view.View;

import com.bumptech.glide.request.animation.ViewPropertyAnimation;

public class MyAnimator implements ViewPropertyAnimation.Animator {

        @Override
        public void animate(View view) {
            final View finalView = view;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    finalView.setScaleX((float) (0.5 + 0.5 * value));
                    finalView.setScaleY((float) (0.5 + 0.5 * value));
                    finalView.setAlpha(value);
                }
            });
            valueAnimator.start();
        }
    }