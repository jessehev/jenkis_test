package com.ut.wb.ui;

import activity.appstoreapplication.utstar.com.ui.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import com.ut.wb.ui.constance.TagConstance;

/**
 * Created by Aria.Lao on 2017/1/17.
 */

public class TagUtil {
  /**
   * 处理左上角标签
   * 0：热门、1：新增、2：普通
   */
  public static void handleHotTag(ImageView img, int tag) {
    int imgRes = -1;
    switch (tag) {
      case TagConstance.HOT_TAG_HOT:
        imgRes = R.mipmap.icon_hot;
        break;
      case TagConstance.HOT_TAG_NEW:
        imgRes = R.mipmap.icon_new;
        break;
      case TagConstance.HOT_TAG_NORMAL:
        img.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        break;
    }
    if (imgRes != -1) {
      img.setImageResource(imgRes);
    }
  }

  /**
   * 处理标签
   * 0：免费、1：普通、2：内购、3：收费、4：限时免费、5：周三限费
   */
  public static void handleTag(ImageView img, int tag, boolean isBuy) {
    int imgRes = -1;
    if (isBuy) {
      //imgRes = R.mipmap.icon_yigou;
    } else {
      //处理标签
      switch (tag) {
        case TagConstance.TAG_FREE:
          //imgRes = R.mipmap.icon_free;
          img.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
          break;
        case TagConstance.TAG_NORMAL:
          img.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
          break;
        case TagConstance.TAG_NEI_GOU:
          imgRes = R.mipmap.icon_neigou;
          break;
        case TagConstance.TAG_CHARGE:
          //imgRes = R.mipmap.icon_sf;
          img.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
          break;
        case TagConstance.TAG_TIME_FREE:
          //imgRes = R.mipmap.icon_xsmf;
          break;
        case TagConstance.TAG_ZSXM:
          imgRes = R.mipmap.icon_zsxm;
          break;
      }
    }
    if (imgRes != -1) {
      img.setImageResource(imgRes);
    }
  }
}
