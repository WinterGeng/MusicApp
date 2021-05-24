package com.geng.lib_image_loader.app;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.geng.lib_image_loader.R;

/**
 * 图片加载类，与外界的唯一通信类，支持为各种view，motification，appwidget,viewgroup加载图片
 */
public class ImageLoaderManager {
    private ImageLoaderManager() {

    }

    private static class SingletonHolder {
        private static ImageLoaderManager instance = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance() {
        return SingletonHolder.instance;
    }

    //为ImageView加载图片
    public void displayImageForView(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).
                asBitmap().
                load(url).
                apply(initCommonRequestOption()).
                //图片加载的切换效果
                        transition(BitmapTransitionOptions.withCrossFade()).
                into(imageView);
    }

    //为imageview加载圆型图片
    public void displayImageForCircle(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView) {
                    //将imageview包装成target
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.
                                create(imageView.getResources(), resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        //占位图和错误图
        options.placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                //缓存策略
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }
}
