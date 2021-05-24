package com.geng.lib_image_loader.app;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.geng.lib_image_loader.R;
import com.geng.lib_image_loader.image.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

    //完成为viewgroup设置背景并模糊处理
    public void displayImageForViewGroup(final ViewGroup group, String url, Boolean doBlur) {
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //在资源准备好时回调这个方法
                        final Bitmap bitmap = resource;
                        Observable.just(resource).map(new Function<Bitmap, Drawable>() {
                            @Override
                            public Drawable apply(Bitmap bitmap) throws Exception {
                                //将bitmap进行模糊处理并转为drawable
                                Drawable drawable = new BitmapDrawable(
                                        Utils.doBlur(resource, 100, doBlur));
                                return drawable;
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) throws Exception {
                                        group.setBackground(drawable);
                                    }
                                });
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
