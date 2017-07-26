package com.utstar.appstoreapplication.activity.manager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by Aria.Lao on 2016/12/23.
 * 动画管理
 */
public class AnimManager extends CommonManager {

  private static volatile AnimManager INSTANCE = null;
  private static final Object LOCK = new Object();

  public static AnimManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new AnimManager();
      }
    }
    return INSTANCE;
  }

  @Override void init() {

  }

  @Override String initName() {
    return "AnimManager";
  }

  @Override void onDestroy() {

  }

  /**
   * 放大
   *
   * @param times 倍数
   */
  public void enlarge(View view, float times) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, times);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, times);
    AnimatorSet set = new AnimatorSet();
    set.play(scaleX).with(scaleY);
    set.setDuration(300);
    set.start();
  }

  /**
   * 缩小
   *
   * @param times 倍数
   */
  public void narrow(View view, float times) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", times, 1.0f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", times, 1.0f);
    AnimatorSet set = new AnimatorSet();
    set.play(scaleX).with(scaleY);
    set.setDuration(300);
    set.start();
  }
}
