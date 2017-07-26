package com.utstar.appstoreapplication.activity.windows.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.arialyy.frame.core.AbsPopupWindow;

/**
 * Created by “Aria.Lao” on 2015/12/3.
 * popupWindow基类
 */
public abstract class BasePopupWindow extends AbsPopupWindow {
  public BasePopupWindow(Context context) {
    super(context);
  }

  public BasePopupWindow(Context context, Drawable background) {
    super(context, background);
  }

  public BasePopupWindow(Context context, Drawable background, Object obj) {
    super(context, background, obj);
  }

  @Override protected void dataCallback(int result, Object obj) {
  }
}