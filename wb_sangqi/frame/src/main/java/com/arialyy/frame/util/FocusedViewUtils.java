//package com.arialyy.frame.util;
//
//import android.animation.Animator;
//import android.animation.ValueAnimator;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * Created by Richard
// */
//public class FocusedViewUtils {
//  public static final float ZOOM_IN = 1.1f;
//  public static final float ZOOM_OUT = 1.0f;
//  public static final int DURATION = 200;
//  private static ValueAnimator focusedFrameAnimator;
//
//  /**
//   * 移动焦点框至指定的VIEW上
//   *
//   * @param focusedFrame 焦点框
//   * @param view 焦点VIEW
//   * @param offsetLeft 焦点框左边距
//   * @param offsetTop 焦点框上边距
//   * @param offsetRight 焦点框右边距
//   * @param offsetBottom 焦点框下边距
//   */
//  public static void setFocusedView(View focusedFrame, View view, int offsetLeft, int offsetTop,
//      int offsetRight, int offsetBottom) {
//    setFocusedView(focusedFrame, view, offsetLeft, offsetTop, offsetRight, offsetBottom, true);
//  }
//
//  public static void setFocusedView(View focusedFrame, int left, int top, int width, int height,
//      int offsetLeft, int offsetTop, int offsetRight, int offsetBottom) {
//    if (focusedFrame != null) {
//      if (focusedFrameAnimator != null && focusedFrameAnimator.isRunning()) {
//        focusedFrameAnimator.end();
//      }
//      FocusedAnimListener listener =
//          new FocusedAnimListener(focusedFrame, left, top, width, height, offsetLeft, offsetTop,
//              offsetRight, offsetBottom, false);
//      focusedFrameAnimator = ValueAnimator.ofFloat(0, 1);
//      focusedFrameAnimator.addUpdateListener(listener);
//      focusedFrameAnimator.addListener(listener);
//      focusedFrameAnimator.setDuration(DURATION);
//      focusedFrameAnimator.start();
//    }
//  }
//
//  public static void setFocusedView(View focusedFrame, View view, int offsetLeft, int offsetTop,
//      int offsetRight, int offsetBottom, boolean zoomIn) {
//    if (view == null) {
//      return;
//    }
//    float scaleX = view.getScaleX();
//    float scaleY = view.getScaleY();
//    if (scaleX != ZOOM_OUT || scaleY != ZOOM_OUT) {
//      return;
//    }
//    if (focusedFrame != null) {
//      if (focusedFrameAnimator != null && focusedFrameAnimator.isRunning()) {
//        focusedFrameAnimator.end();
//      }
//      FocusedAnimListener listener =
//          new FocusedAnimListener(focusedFrame, view, offsetLeft, offsetTop, offsetRight,
//              offsetBottom, zoomIn);
//      focusedFrameAnimator = ValueAnimator.ofFloat(0, 1);
//      focusedFrameAnimator.addUpdateListener(listener);
//      focusedFrameAnimator.addListener(listener);
//      focusedFrameAnimator.setDuration(DURATION);
//      focusedFrameAnimator.start();
//    }
//    ValueAnimator lastValueAnimator = (ValueAnimator) view.getTag(R.id.view_anim_tag);
//    if (lastValueAnimator != null && lastValueAnimator.isRunning()) {
//      lastValueAnimator.end();
//    }
//    if (zoomIn) {
//      ViewAnimListener listener = new ViewAnimListener(view);
//      ValueAnimator viewZoomInAnim = ValueAnimator.ofFloat(ZOOM_OUT, ZOOM_IN);
//      viewZoomInAnim.addUpdateListener(listener);
//      viewZoomInAnim.addListener(listener);
//      viewZoomInAnim.setDuration(DURATION);
//      viewZoomInAnim.start();
//      view.setTag(R.id.view_anim_tag, viewZoomInAnim);
//    }
//  }
//
//  /**
//   * 失焦缩小动画
//   */
//  public static void setBlurView(View view) {
//    if (view == null) {
//      return;
//    }
//    ValueAnimator lastValueAnimator = (ValueAnimator) view.getTag(R.id.view_anim_tag);
//    if (lastValueAnimator != null && lastValueAnimator.isRunning()) {
//      lastValueAnimator.end();
//    }
//    float scaleX = view.getScaleX();
//    float scaleY = view.getScaleY();
//    if (scaleX > ZOOM_OUT || scaleY > ZOOM_OUT) {
//      ViewAnimListener listener = new ViewAnimListener(view);
//      ValueAnimator viewZoomOutAnim = ValueAnimator.ofFloat(ZOOM_IN, ZOOM_OUT);
//      viewZoomOutAnim.addUpdateListener(listener);
//      viewZoomOutAnim.addListener(listener);
//      viewZoomOutAnim.setDuration(DURATION);
//      viewZoomOutAnim.start();
//      view.setTag(R.id.view_anim_tag, viewZoomOutAnim);
//    }
//  }
//
//  /**
//   * 是否允许滚动
//   */
//  public static boolean arrowScroll(View view, int direct) {
//    int right = view.getRight();
//    int left = view.getLeft();
//    int top = view.getTop();
//    int bottom = view.getBottom();
//
//    ViewGroup parentView = (ViewGroup) view.getParent();
//    int width = parentView.getWidth();
//    int height = parentView.getHeight();
//    int paddingLeft = parentView.getPaddingLeft();
//    int paddingRight = parentView.getPaddingRight();
//    int paddingTop = parentView.getPaddingTop();
//    int paddingBottom = parentView.getPaddingBottom();
//    if (direct == View.FOCUS_UP) {
//      return top < paddingTop;
//    } else if (direct == View.FOCUS_DOWN) {
//      return bottom > height - paddingBottom;
//    } else if (direct == View.FOCUS_RIGHT) {
//      return right > width - paddingRight;
//    } else {
//      return direct == View.FOCUS_LEFT && left < paddingLeft;
//    }
//  }
//
//  /**
//   * 是否允许滚动
//   */
//  @Deprecated public static boolean arrowScroll(View view) {
//    int right = view.getRight();
//    int left = view.getLeft();
//    int top = view.getTop();
//    int bottom = view.getBottom();
//
//    ViewGroup parentView = (ViewGroup) view.getParent();
//    int width = parentView.getWidth();
//    int height = parentView.getHeight();
//    int paddingLeft = parentView.getPaddingLeft();
//    int paddingRight = parentView.getPaddingRight();
//    int paddingTop = parentView.getPaddingTop();
//    int paddingBottom = parentView.getPaddingBottom();
//    return left < paddingLeft
//        || right > width - paddingRight
//        || top < paddingTop
//        || bottom > height - paddingBottom;
//  }
//
//  static class FocusedAnimListener
//      implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
//    private View focusedView;
//    private View view;
//    private int offsetLeft;
//    private int offsetTop;
//    private int offsetRight;
//    private int offsetBottom;
//    private int destWidth;
//    private int destHeight;
//    private int destLeft;
//    private int destTop;
//    private int focusWidth;
//    private int focusHeight;
//    private int[] focusedLeftAndTop;
//    private int LEFT = 0;
//    private int TOP = 1;
//    private boolean isZoom;
//
//    public FocusedAnimListener(View focusedView, View view, int offsetLeft, int offsetTop,
//        int offsetRight, int offsetBottom, boolean isZoom) {
//      this.focusedView = focusedView;
//      this.view = view;
//      this.offsetLeft = offsetLeft;
//      this.offsetTop = offsetTop;
//      this.offsetRight = offsetRight;
//      this.offsetBottom = offsetBottom;
//      this.isZoom = isZoom;
//      compute();
//    }
//
//    public FocusedAnimListener(View focusedView, int left, int top, int width, int height,
//        int offsetLeft, int offsetTop, int offsetRight, int offsetBottom, boolean isZoom) {
//      this.focusedView = focusedView;
//      this.offsetLeft = offsetLeft;
//      this.offsetTop = offsetTop;
//      this.offsetRight = offsetRight;
//      this.offsetBottom = offsetBottom;
//      this.isZoom = isZoom;
//      compute(left, top, width, height);
//    }
//
//    private void compute(int left, int top, int width, int height) {
//      focusedLeftAndTop = new int[2];
//      focusedView.getLocationOnScreen(focusedLeftAndTop);
//      focusWidth = focusedView.getWidth();
//      focusHeight = focusedView.getHeight();
//
//      if (isZoom) {
//        float incrementWidth = (width * ZOOM_IN - width) / 2;
//        float incrementHeight = (height * ZOOM_IN - height) / 2;
//        destWidth = (int) (width * ZOOM_IN + offsetLeft + offsetRight);
//        destHeight = (int) (height * ZOOM_IN + offsetTop + offsetBottom);
//        destLeft = (int) (left - offsetLeft - incrementWidth);
//        destTop = (int) (top - offsetTop - incrementHeight);
//      } else {
//        destWidth = width + offsetLeft + offsetRight;
//        destHeight = height + offsetTop + offsetBottom;
//        destLeft = left - offsetLeft;
//        destTop = top - offsetTop;
//      }
//    }
//
//    private void compute() {
//      focusedLeftAndTop = new int[2];
//      focusedView.getLocationOnScreen(focusedLeftAndTop);
//      focusWidth = focusedView.getWidth();
//      focusHeight = focusedView.getHeight();
//
//      int[] viewLeftAndTop = new int[2];
//      view.getLocationOnScreen(viewLeftAndTop);
//      int itemWidth = view.getWidth();
//      int itemHeight = view.getHeight();
//
//      if (isZoom) {
//        float incrementWidth = (itemWidth * ZOOM_IN - itemWidth) / 2;
//        float incrementHeight = (itemHeight * ZOOM_IN - itemHeight) / 2;
//        destWidth = (int) (itemWidth * ZOOM_IN + offsetLeft + offsetRight);
//        destHeight = (int) (itemHeight * ZOOM_IN + offsetTop + offsetBottom);
//        destLeft = (int) (viewLeftAndTop[LEFT] - offsetLeft - incrementWidth);
//        destTop = (int) (viewLeftAndTop[TOP] - offsetTop - incrementHeight);
//      } else {
//        destWidth = itemWidth + offsetLeft + offsetRight;
//        destHeight = itemHeight + offsetTop + offsetBottom;
//        destLeft = viewLeftAndTop[LEFT] - offsetLeft;
//        destTop = viewLeftAndTop[TOP] - offsetTop;
//      }
//    }
//
//    @Override public void onAnimationUpdate(ValueAnimator animation) {
//      float value = (float) animation.getAnimatedValue();
//      int width = (int) ((destWidth - focusWidth) * value + focusWidth);
//      int height = (int) ((destHeight - focusHeight) * value + focusHeight);
//      int left = (int) ((destLeft - focusedLeftAndTop[LEFT]) * value + focusedLeftAndTop[LEFT]);
//      int top = (int) ((destTop - focusedLeftAndTop[TOP]) * value + focusedLeftAndTop[TOP]);
//
//      ViewGroup.MarginLayoutParams params =
//          (ViewGroup.MarginLayoutParams) focusedView.getLayoutParams();
//      params.leftMargin = left;
//      params.topMargin = top;
//      params.width = width;
//      params.height = height;
//
//      focusedView.setLayoutParams(params);
//    }
//
//    @Override public void onAnimationStart(Animator animation) {
//
//    }
//
//    @Override public void onAnimationEnd(Animator animation) {
//      ViewGroup.MarginLayoutParams params =
//          (ViewGroup.MarginLayoutParams) focusedView.getLayoutParams();
//      params.leftMargin = destLeft;
//      params.topMargin = destTop;
//      params.width = destWidth;
//      params.height = destHeight;
//
//      focusedView.setLayoutParams(params);
//    }
//
//    @Override public void onAnimationCancel(Animator animation) {
//
//    }
//
//    @Override public void onAnimationRepeat(Animator animation) {
//
//    }
//  }
//
//  static class ViewAnimListener
//      implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
//    private View view;
//
//    public ViewAnimListener(View view) {
//      this.view = view;
//    }
//
//    @Override public void onAnimationUpdate(ValueAnimator animation) {
//      float value = (float) animation.getAnimatedValue();
//      view.setScaleX(value);
//      view.setScaleY(value);
//    }
//
//    @Override public void onAnimationCancel(Animator animation) {
//
//    }
//
//    @Override public void onAnimationStart(Animator animation) {
//
//    }
//
//    @Override public void onAnimationEnd(Animator animation) {
//      ValueAnimator valueAnimator = (ValueAnimator) animation;
//      float value = (float) valueAnimator.getAnimatedValue();
//      view.setScaleX(value);
//      view.setScaleY(value);
//    }
//
//    @Override public void onAnimationRepeat(Animator animation) {
//
//    }
//  }
//}
