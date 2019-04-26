package com.bumptech.glide.request.target;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;

/**
 * A one time use {@link Target} class that loads a resource into memory and then
 * clears itself.
 *
 * @param <Z> The type of resource that will be loaded into memory.
 */
public final class PreloadTarget<Z> extends SimpleTarget<Z> {

    /**
     * Returns a PreloadTarget.
     *
     * @param width The width in pixels of the desired resource.
     * @param height The height in pixels of the desired resource.
     * @param <Z> The type of the desired resource.
     */
    public static <Z> PreloadTarget<Z> obtain(int width, int height) {
        return new PreloadTarget<Z>(width, height);
    }

    private PreloadTarget(int width, int height) {
        super(width, height);
    }

    @Override
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
//        onResourceReady()方法中也没做什么事情，只是调用了Glide.clear()方法。
//        这里的Glide.clear()并不是清空缓存的意思，而是表示加载已完成，释放资源的意思
//        其实PreloadTarget的思想和我们刚才提到设计思路是一样的，就是什么都不做就可以了。因为图片加载完成之后只将它缓存而不去显示它，那不就相当于预加载了嘛
        Glide.clear(this);
    }
}
