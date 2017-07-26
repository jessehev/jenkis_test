package com.utstar.appstoreapplication.activity.commons.widget.adve_banner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import com.utstar.appstoreapplication.activity.R;

/**
 * Created by JesseHev on 2017/1/10.
 * 广告轮播
 */

public class AdverView extends LinearLayout {

  //控件高度
  private float mAdverHeight = 0f;
  //默认轮播间隔时间
  private int mIntervalTime = 4000;
  //默认动画间隔时间
  private int mAnimDuration = 1000;

  private AdVerAdpater mAdapter;

  private final float mAdverMaxHeight = 50;

  private View mFirstView;
  private View mSecondView;

  //轮播的下标
  private int mPosition;
  //线程的标识
  private boolean isStarted;

  public AdverView(Context context) {
    this(context, null);
  }

  public AdverView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AdverView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    setOrientation(VERTICAL);
    // mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AdverView);
    mAdverHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mAdverMaxHeight,
        getResources().getDisplayMetrics());

    mIntervalTime = array.getInteger(R.styleable.AdverView_intervalTime, mIntervalTime);
    mAnimDuration = array.getInteger(R.styleable.AdverView_animDuration, mAnimDuration);

    if (mIntervalTime <= mAnimDuration) {
      mIntervalTime = mAnimDuration;
    }
    array.recycle();
  }

  public void setAdapter(AdVerAdpater adapter) {
    this.mAdapter = adapter;
    upAdapter();
  }

  /**
   * 开始轮播
   */
  public void start() {
    if (!isStarted && mAdapter.getCount() > 1) {
      isStarted = true;
      postDelayed(mRunnable, mIntervalTime);//间隔mIntervalTime刷新一次UI
    }
  }

  /**
   * 停止滚动
   */
  public void stop() {
    removeCallbacks(mRunnable);
    isStarted = false;
  }

  private void upAdapter() {
    removeAllViews();
    //只有一条数据,不滚动
    if (mAdapter.getCount() == 1) {
      mFirstView = mAdapter.getView(this);
      mAdapter.setItem(mFirstView, mAdapter.getItem(0));
      addView(mFirstView);
    } else {
      mFirstView = mAdapter.getView(this);
      mSecondView = mAdapter.getView(this);
      mAdapter.setItem(mFirstView, mAdapter.getItem(0));
      mAdapter.setItem(mSecondView, mAdapter.getItem(1));

      addView(mFirstView);
      addView(mSecondView);
      mPosition = 1;
      isStarted = false;
    }
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (LayoutParams.WRAP_CONTENT == getLayoutParams().height) {
      getLayoutParams().height = (int) mAdverHeight;
    } else {
      mAdverHeight = getHeight();
    }
    if (mFirstView != null) {
      mFirstView.getLayoutParams().height = (int) mAdverHeight;
    }
    if (mSecondView != null) {
      mSecondView.getLayoutParams().height = (int) mAdverHeight;
    }
  }

  private void performSwitch() {
    ObjectAnimator animator1 = ObjectAnimator.ofFloat(mFirstView, "translationY",
        mFirstView.getTranslationY() - mAdverHeight);
    ObjectAnimator animator2 = ObjectAnimator.ofFloat(mSecondView, "translationY",
        mSecondView.getTranslationY() - mAdverHeight);
    AnimatorSet set = new AnimatorSet();
    set.playTogether(animator1, animator2);
    set.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {//动画结束回调
        mFirstView.setTranslationY(0);
        mSecondView.setTranslationY(0);
        View removedView = getChildAt(0);//获得第一个子布局
        mPosition++;
        //设置显示的布局 复用 removedView
        mAdapter.setItem(removedView, mAdapter.getItem(mPosition % mAdapter.getCount()));
        //移除前一个view
        removeView(removedView);
        //添加下一个view
        addView(removedView, 1);
      }
    });
    set.setDuration(mAnimDuration);
    set.start();
  }

  private AnimRunnable mRunnable = new AnimRunnable();

  private class AnimRunnable implements Runnable {
    @Override public void run() {
      performSwitch();
      postDelayed(this, mIntervalTime);
    }
  }

  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    //停止滚动
    stop();
  }

  protected void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }
}
