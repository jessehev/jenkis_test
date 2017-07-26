package com.ut.wb.ui;

import activity.appstoreapplication.utstar.com.ui.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Utils {
  public static final int SOUND_KEYSTONE_KEY = 1;
  public static final int SOUND_ERROR_KEY = 0;

  /**
   * 获取后缀名
   */
  public static String getFileExtensionName(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return "";
    }
    int endP = fileName.lastIndexOf(".");
    return endP > -1 ? fileName.substring(endP + 1, fileName.length()) : "";
  }

  public static void setImg(Context context, ImageView img, String url, int radius) {
    setImg(context, img, url, R.mipmap.place_def, R.mipmap.place_def, radius);
  }

  public static void setImg(Context context, ImageView img, int imgId, int radius) {
    setImg(context, img, imgId, R.mipmap.place_def, R.mipmap.place_def, radius);
  }

  /**
   * 设置图片
   *
   * @param img 图片控件
   * @param imgId 资源图片
   * @param placeId 加载等待图片
   * @param errorId 错误填充图片
   * @param radius 圆角半径
   */
  public static void setImg(Context context, ImageView img, int imgId, int placeId, int errorId,
      int radius) {
    DrawableRequestBuilder builder =
        Glide.with(context).load(imgId).placeholder(placeId).error(errorId).centerCrop()
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
  public static void setImg(Context context, ImageView img, String url, int placeId, int errorId,
      int radius) {
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
   * 下载档位控制
   */
  public static String downloadCountHelp(int count) {
    String downloadText;
    if (count <= 5000) {
      downloadText = "小于5000";
    } else if (5000 <= count & count < 10000) {
      downloadText = "5000+";
    } else if (10000 <= count && count < 50000) {
      downloadText = "1万+";
    } else if (50000 <= count && count < 100000) {
      downloadText = "5万+";
    } else if (100000 <= count && count < 200000) {
      downloadText = "10万+";
    } else if (200000 <= count && count < 500000) {
      downloadText = "20万+";
    } else if (500000 <= count && count < 1000000) {
      downloadText = "50万+";
    } else {
      downloadText = "100万+";
    }
    //Log.i("CommondHelp", "CommondHelp====" + "挡位" + downloadText + "数量==" + count);
    return downloadText;
  }

  public static void playKeySound(View view, int soundKey) {
    if (null != view) {
      if (soundKey == SOUND_KEYSTONE_KEY) {
        view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
      } else if (soundKey == SOUND_ERROR_KEY) {
        view.playSoundEffect(5);
      }
    }
  }
}
