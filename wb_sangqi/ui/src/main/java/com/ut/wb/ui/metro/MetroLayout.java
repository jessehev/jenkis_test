package com.ut.wb.ui.metro;

import activity.appstoreapplication.utstar.com.ui.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/12. Metro 布局
 */
public class MetroLayout extends FrameLayout
    implements View.OnFocusChangeListener, View.OnClickListener {
  private static final String TAG = "MetroLayout";
  public static final int NORMAL_VERTICAL = 0; //标准竖向item
  public static final int NORMAL_HORIZONTAL = 1; //标准横向item
  public static final int NORMAL_SQUARE = 2; //正方形的item
  public static final int NORMAL_MAX_VERTICAL = 3; //大型的竖直item
  public static final int SIMPLE_VERTICAL = 4; //简单的竖向item
  public static final int SIMPLE_HORIZONTAL = 5; //简单横向item
  public static final int SIMPLE_SQUARE = 6; //简单正方形item

  private static int ITEM_V_WIDTH = -1;
  private static int ITEM_V_HEIGHT = -1;
  private static int ITEM_MAX_V_WIDTH = -1;
  private static int ITEM_MAX_V_HEIGHT = -1;
  private static int ITEM_H_WIDTH = -1;
  private static int ITEM_H_HEIGHT = -1;
  private static int ITEM_NORMAL_SIZE = -1;
  private static int mirror_ref_height = -1;
  private static int DIVIDE_SIZE = 6;
  private float mDensityScale = 1.0f;
  private int WIDTH, HEIGHT;
  private OnMetroClickListener mListener;
  private SparseArray<BaseItemView> mViews = new SparseArray<>();
  private BaseItemView mFirstView;

  public interface OnMetroClickListener {
    public void onMetroClick(View view, int position, MetroItemEntity entity);
  }

  private List<MetroItemEntity> mData = new ArrayList<>();
  int[] rowOffset = new int[2];
  AnimatorSet mScaleAnimator;

  public MetroLayout(Context context) {
    super(context);
  }

  public MetroLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MetroLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setOnMetroClickListener(OnMetroClickListener listener) {
    mListener = listener;
  }

  private void init() {
    if (ITEM_V_WIDTH == -1) {
      int VH = ViewGroup.LayoutParams.MATCH_PARENT;
      HEIGHT = getResources().getDimensionPixelSize(R.dimen.dimen_660dp);
      DIVIDE_SIZE = getResources().getDimensionPixelSize(R.dimen.dimen_20dp);
      int normalSize = (HEIGHT - DIVIDE_SIZE) / 2;
      ITEM_V_WIDTH = normalSize;
      ITEM_V_HEIGHT = HEIGHT;
      ITEM_MAX_V_WIDTH = getResources().getDimensionPixelSize(R.dimen.dimen_420dp);
      ITEM_MAX_V_HEIGHT = HEIGHT;
      ITEM_H_WIDTH = normalSize * 2 + DIVIDE_SIZE;
      ITEM_H_HEIGHT = normalSize;
      ITEM_NORMAL_SIZE = normalSize;
      mirror_ref_height = getResources().getDimensionPixelSize(R.dimen.mirror_ref_height);
    }
    setClipChildren(false);
  }

  public View getLastFocusedView() {
    return lastFocusedView;
  }

  public void setItem(List<MetroItemEntity> data) {
    if (data == null || data.size() <= 0) {
      return;
    }
    init();
    mData.clear();
    mData.addAll(data);
    mViews.clear();
    rowOffset[0] = 0;
    rowOffset[1] = 0;
    removeAllViews();
    int i = 0;
    for (MetroItemEntity item : mData) {
      addChild(item, i);
      i++;
    }
    invalidate();
  }

  public void update(List<MetroItemEntity> data) {
    mData.clear();
    mData.addAll(data);
    for (int i = 0, len = mViews.size(); i < len; i++) {
      BaseItemView itemView = mViews.get(i);
      MetroItemEntity entity = data.get(i);
      switch (entity.type) {
        case NORMAL_VERTICAL:
        case NORMAL_HORIZONTAL:
        case NORMAL_SQUARE:
        case NORMAL_MAX_VERTICAL:
          itemView.update(entity.normalItemData);
          break;
        case SIMPLE_VERTICAL:
        case SIMPLE_HORIZONTAL:
        case SIMPLE_SQUARE:
          itemView.update(entity.simpleItemData);
          break;
      }
    }
  }

  public void addChild(MetroItemEntity entity, int position) {
    BaseItemView child = null;
    LayoutParams flp = null;
    int padding = DIVIDE_SIZE;
    switch (entity.type) {
      case NORMAL_MAX_VERTICAL:
        flp = new LayoutParams((int) (ITEM_MAX_V_WIDTH * mDensityScale),
            (int) (ITEM_MAX_V_HEIGHT * mDensityScale));
        child = new NormalMetroItemView(getContext());
        child.setLayoutParams(flp);
        flp.leftMargin = rowOffset[0];
        flp.rightMargin = getPaddingRight();
        if(rowOffset[0] == 0 && mFirstView == null){
          mFirstView = child;
        }
        rowOffset[0] += ITEM_MAX_V_WIDTH * mDensityScale + padding;
        rowOffset[1] = rowOffset[0];
        break;
      case NORMAL_VERTICAL:
      case SIMPLE_VERTICAL:
        flp = new LayoutParams((int) (ITEM_V_WIDTH * mDensityScale),
            (int) (ITEM_V_HEIGHT * mDensityScale));
        if (entity.type == NORMAL_VERTICAL) {
          child = new NormalMetroItemView(getContext());
        } else {
          child = new SimpleMetroItemView(getContext());
        }
        child.setLayoutParams(flp);
        flp.leftMargin = rowOffset[0];
        flp.rightMargin = getPaddingRight();
        if(rowOffset[0] == 0 && mFirstView == null){
          mFirstView = child;
        }
        rowOffset[0] += ITEM_V_WIDTH * mDensityScale + padding;
        rowOffset[1] = rowOffset[0];
        break;
      case NORMAL_HORIZONTAL:
      case SIMPLE_HORIZONTAL:
        flp = new LayoutParams((int) (ITEM_H_WIDTH * mDensityScale),
            (int) (ITEM_H_HEIGHT * mDensityScale));
        if (entity.type == NORMAL_HORIZONTAL) {
          child = new NormalMetroItemView(getContext());
        } else {
          child = new SimpleMetroItemView(getContext());
        }
        if (entity.row == 0) {
          flp.leftMargin = rowOffset[0];
          flp.rightMargin = getPaddingRight();
          if(rowOffset[0] == 0 && mFirstView == null){
            mFirstView = child;
          }
          rowOffset[0] += ITEM_H_WIDTH * mDensityScale + padding;
        } else {
          flp.leftMargin = rowOffset[1];
          flp.rightMargin = getPaddingRight();
          flp.topMargin += ITEM_NORMAL_SIZE * mDensityScale + padding;
          rowOffset[1] += ITEM_H_WIDTH * mDensityScale + padding;
        }
        break;
      case NORMAL_SQUARE:
      case SIMPLE_SQUARE:
        flp = new LayoutParams((int) (ITEM_NORMAL_SIZE * mDensityScale),
            (int) (ITEM_NORMAL_SIZE * mDensityScale));
        if (entity.type == NORMAL_SQUARE) {
          child = new NormalMetroItemView(getContext());
        } else {
          child = new SimpleMetroItemView(getContext());
        }
        if (entity.row == 0) {
          flp.leftMargin = rowOffset[0];
          flp.rightMargin = getPaddingRight();
          if(rowOffset[0] == 0 && mFirstView == null){
            mFirstView = child;
          }
          rowOffset[0] += ITEM_NORMAL_SIZE * mDensityScale + padding;
        } else {
          flp.leftMargin = rowOffset[1];
          flp.rightMargin = getPaddingRight();
          flp.topMargin += ITEM_NORMAL_SIZE * mDensityScale + padding;
          rowOffset[1] += ITEM_NORMAL_SIZE * mDensityScale + padding;
        }
        break;
    }
    if (child != null) {
      child.setFocusable(true);
      child.setOnFocusChangeListener(this);
      child.setOnClickListener(this);
      addView(child, flp);
      switch (entity.type) {
        case NORMAL_VERTICAL:
        case NORMAL_HORIZONTAL:
        case NORMAL_SQUARE:
        case NORMAL_MAX_VERTICAL:
          child.bindData(entity.type, entity.normalItemData);
          break;
        case SIMPLE_VERTICAL:
        case SIMPLE_HORIZONTAL:
        case SIMPLE_SQUARE:
          child.bindData(entity.type, entity.simpleItemData);
          break;
      }
      mViews.append(position, child);
    }
  }

  public View getFirstView() {
    return mFirstView;
  }

  /**
   * 子item获取焦点
   */
  public void requestChildFocus(int position) {
    if (position < 0 || position >= getChildCount()) {
      Log.w(TAG, "position 错误");
      return;
    }
    mViews.get(position).requestFocus();
  }

  private View lastFocusedView;

  @Override
  protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
    if (lastFocusedView != null && lastFocusedView.requestFocus(direction, previouslyFocusedRect)) {
      return true;
    }

    int index;
    int increment;
    int end;
    int count = this.getChildCount();
    if ((direction & FOCUS_FORWARD) != 0) {
      index = 0;
      increment = 1;
      end = count;
    } else {
      index = count - 1;
      increment = -1;
      end = -1;
    }

    for (int i = index; i != end; i += increment) {
      View child = this.getChildAt(i);
      if (child.requestFocus(direction, previouslyFocusedRect)) {
        return true;
      }
    }
    return false;
  }

  @Override public void onFocusChange(final View v, boolean hasFocus) {
    if (mScaleAnimator != null) mScaleAnimator.end();
    if (v instanceof BaseItemView) {
      ((BaseItemView) v).hasFocus(hasFocus);
    }
    if (hasFocus) {
      lastFocusedView = v;
      bringChildToFront(v);
      invalidate();
      ObjectAnimator animX =
          ObjectAnimator.ofFloat(v, "ScaleX", new float[] { 1.0F, 1.15F }).setDuration(200);
      ObjectAnimator animY =
          ObjectAnimator.ofFloat(v, "ScaleY", new float[] { 1.0F, 1.15F }).setDuration(200);
      mScaleAnimator = new AnimatorSet();
      mScaleAnimator.play(animX).with(animY);
      mScaleAnimator.start();
    } else {
      v.setScaleX(1.0f);
      v.setScaleY(1.0f);
    }
  }

  @Override public void onClick(View v) {
    if (v instanceof BaseItemView) {
      int position = mViews.indexOfValue((BaseItemView) v);
      if (mListener != null) {
        if (position == -1) {
          Log.d(TAG, "没有找到被点击的View");
          return;
        }
        mListener.onMetroClick(v, position, mData.get(position));
      }
    }
  }
}
