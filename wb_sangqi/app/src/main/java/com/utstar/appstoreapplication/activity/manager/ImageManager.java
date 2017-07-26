package com.utstar.appstoreapplication.activity.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.arialyy.frame.util.show.FL;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameClassifyEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.game_hall.game_classify.GameClassifyActivity;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import com.utstar.appstoreapplication.activity.windows.monthly_payment.MothlyPayActivity;
import java.util.concurrent.ExecutionException;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by “Aria.Lao” on 2016/10/26.
 * 图片管理工具
 */
public class ImageManager extends CommonManager {
  private static final Object LOCK = new Object();
  private static volatile ImageManager INSTANCE = null;
  private static final String TAG = "ImageManager";

  public static ImageManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new ImageManager();
      }
    }
    return INSTANCE;
  }

  /**
   * 加载普通网络图片
   */
  public void loadImg(ImageView img, String url) {
    setImg(img, url, R.mipmap.place_def, R.mipmap.place_def, 0);
  }

  /**
   * 加载资源图片
   */
  public void loadImg(ImageView img, int resourceId) {
    setImg(img, resourceId, R.mipmap.place_def, R.mipmap.place_def, 0);
  }

  /**
   * 加载图片网络图片，并进行圆角处理
   */
  public void loadRoundedImg(ImageView img, String url, int radius) {
    setImg(img, url, R.mipmap.place_def, R.mipmap.place_def, radius);
  }

  /**
   * 加载资源图片, 并进行圆角处理
   */
  public void loadRoundedImg(ImageView img, int resourceId, int radius) {
    setImg(img, resourceId, R.mipmap.place_def, R.mipmap.place_def, radius);
  }

  /**
   * 加载图片网络图片，并进行圆角处理
   */
  public void loadRoundedImgNoScaleType(ImageView img, String url, int radius) {
    setImgScaleType(img, url, R.mipmap.place_def, R.mipmap.place_def, radius);
  }

  /**
   * 设置图片
   *
   * @param img 图片控件
   * @param url 图片链接
   * @param placeId 加载等待图片
   * @param errorId 错误填充图片
   * @param radius 圆角半径
   */
  public void setImg(ImageView img, String url, int placeId, int errorId, int radius) {
    final Context context = BaseApp.context;
    DrawableRequestBuilder builder =
        Glide.with(context).load(url).placeholder(placeId).error(errorId).centerCrop()
            //.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE);
    if (radius > 0) {
      builder.bitmapTransform(new RoundedCornersTransformation(context, radius, 0,
          RoundedCornersTransformation.CornerType.ALL)).into(img);
    } else {
      builder.into(img);
    }
  }

  /**
   * 设置图片
   *
   * @param img 图片控件
   * @param url 图片链接
   * @param placeId 加载等待图片
   * @param errorId 错误填充图片
   * @param radius 圆角半径
   */
  public void setImgScaleType(ImageView img, String url, int placeId, int errorId, int radius) {
    final Context context = BaseApp.context;
    DrawableRequestBuilder builder =
        Glide.with(context).load(url).placeholder(placeId).error(errorId)
            //.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE);
    if (radius > 0) {
      builder.bitmapTransform(new RoundedCornersTransformation(context, radius, 0,
          RoundedCornersTransformation.CornerType.ALL)).into(img);
    } else {
      builder.into(img);
    }
  }

  /**
   * 设置图片
   *
   * @param img 图片控件
   * @param resourceId 资源ID
   * @param placeId 加载等待图片
   * @param errorId 错误填充图片
   * @param radius 圆角半径
   */
  public void setImg(ImageView img, int resourceId, int placeId, int errorId, int radius) {
    final Context context = BaseApp.context;
    DrawableRequestBuilder builder =
        Glide.with(context).load(resourceId).placeholder(placeId).error(errorId).centerCrop()
            //.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE);
    if (radius > 0) {
      builder.bitmapTransform(new RoundedCornersTransformation(context, radius, 0,
          RoundedCornersTransformation.CornerType.ALL)).into(img);
    } else {
      builder.into(img);
    }
  }

  /**
   * @param img 图片控件
   * @param url 图片链接
   */
  public void setImg(ImageView img, String url) {
    final Context context = BaseApp.context;
    Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade()
        // .placeholder(R.drawable.no_pic_default)
        // .error(R.drawable.no_pic_default)
        .into(img);
  }

  /**
   * 下载图片
   *
   * @param url 图片链接
   * @return bitmap
   */
  public Bitmap downLoadImg(String url) {
    final Context context = BaseApp.context;
    Bitmap bitmap = null;
    try {
      bitmap = Glide.with(context)
          .load(url)
          .asBitmap()
          .centerCrop()
          .diskCacheStrategy(DiskCacheStrategy.SOURCE)
          .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
          .get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return bitmap;
  }

  /**
   * 清除图片
   */
  public void clear(ImageView img) {
    Glide.clear(img);
  }

  @Override void init() {

  }

  @Override String initName() {
    return "图片管理工具";
  }

  @Override void onDestroy() {
  }

  /**
   * 配置背景图
   */
  public void loadBackground(View view, int img) {
    try {
      Glide.with(view.getContext())
          .load(img)
          .diskCacheStrategy(DiskCacheStrategy.SOURCE)
          .into(new SimpleTarget<GlideDrawable>() {
            @Override public void onResourceReady(GlideDrawable resource,
                GlideAnimation<? super GlideDrawable> glideAnimation) {
              view.setBackgroundDrawable(resource);
            }
          });
    } catch (Exception e) {
      FL.d(TAG, "loadBackground error");
    }
  }

  /**
   * 配置背景图
   */
  public void loadBackground(Activity activity, ModuleBgEntity entity) {
    try {
      Glide.with(activity)  //BaseApp.context 全局context
          .load(entity.getUrl())
          .diskCacheStrategy(DiskCacheStrategy.SOURCE)
          .placeholder(R.mipmap.bg_base)
          .error(R.mipmap.bg_base)
          .into(new SimpleTarget<GlideDrawable>() {
            @Override public void onResourceReady(GlideDrawable resource,
                GlideAnimation<? super GlideDrawable> glideAnimation) {
              ((BaseActivity) activity).getRootView().setBackground(resource);
            }
          });
    } catch (Exception e) {
      FL.d(TAG, "loadBackground error");
    }
  }
}
