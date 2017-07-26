package com.ut.wb.ui.TabLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/9. tab导航栏
 */
public class TabLayout extends LinearLayout {
  private static final String TAG = "TabLayout";
  private List<TabEntity> mTabs = new ArrayList<>();
  private int mCurrentPosition = 0;
  private OnTabLayoutSelectedListener mListener;
  private SparseArray<View> mViews = new SparseArray<>();

  /**
   * 导航栏监听
   */
  public interface OnTabLayoutSelectedListener {
    /**
     * item被选中回调
     *
     * @param position 当前item位置
     * @param view 当前item
     */
    public void onItemSelected(int position, View view);

    public void onDown(int position, View view);
  }

  public TabLayout(Context context) {
    this(context, null);
  }

  public TabLayout(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void setOnTabLayoutSelectedListener(OnTabLayoutSelectedListener listener) {
    this.mListener = listener;
  }

  private void init() {
    setOrientation(HORIZONTAL);
    setFocusable(true);
    getViewTreeObserver().addOnGlobalFocusChangeListener(
        new ViewTreeObserver.OnGlobalFocusChangeListener() {
          @Override public void onGlobalFocusChanged(View oldFocus, View newFocus) {
            showTab(hasFocus());
          }
        });
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    int keyCode = event.getKeyCode();
    if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
        || keyCode == KeyEvent.KEYCODE_PAGE_UP
        || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
      if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
        //if (mCurrentPosition <= 0) {
        if (mCurrentPosition < 0) {
          mCurrentPosition = 0;
          return super.dispatchKeyEvent(event);
        }
        mCurrentPosition--;
        //if (mCurrentPosition < 0) mCurrentPosition = 0;
        if (mCurrentPosition < 0) mCurrentPosition = getChildCount() - 1; //跳转最右
        setSelected(mCurrentPosition);
        if (mListener != null) {
          mListener.onItemSelected(mCurrentPosition, getChildAt(mCurrentPosition));
        }
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        //if (mCurrentPosition >= getChildCount() - 1) {
        if (mCurrentPosition > getChildCount() - 1) {
          mCurrentPosition = getChildCount() - 1;
          return super.dispatchKeyEvent(event);
        }
        mCurrentPosition++;
        //if (mCurrentPosition >= getChildCount()) mCurrentPosition = getChildCount() - 1;
        if (mCurrentPosition >= getChildCount()) mCurrentPosition = 0;  //跳转最左
        setSelected(mCurrentPosition);
        if (mListener != null) {
          mListener.onItemSelected(mCurrentPosition, getChildAt(mCurrentPosition));
        }
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        //clearFocus();
        if (mListener != null) {
          mListener.onDown(mCurrentPosition, getChildAt(mCurrentPosition));
          showTab(false);
        }
        return true;
        //return super.dispatchKeyEvent(event);
      }
      return true;
    }
    return super.dispatchKeyEvent(event);
  }

  public void showTab(boolean show) {
    TabView tabView = (TabView) mViews.get(mCurrentPosition);
    if (tabView != null) {
      tabView.getBar().setVisibility(show ? VISIBLE : INVISIBLE);
    }
  }

  /**
   * 设置选中Tab
   */
  public void setSelected(int position) {
    setSelected(position, true);
  }

  /**
   * 设置选中Tab
   *
   * @param useAnim 是否使用动画
   */
  public void setSelected(int position, boolean useAnim) {
    if (position < 0 || position >= getChildCount()) {
      Log.w(TAG, "position 错误");
      return;
    }
    mCurrentPosition = position;
    for (int i = 0, len = getChildCount(); i < len; i++) {
      TabView view = (TabView) getChildAt(i);
      if (position != i && view.isSelected()) {
        view.setSelected(false, useAnim);
      }
      if (position == i) {
        view.setSelected(true, useAnim);
      }
    }
    showTab(true);
  }

  /**
   * 设置tab
   */
  public void setTab(List<TabEntity> tabs) {
    if (tabs == null || tabs.size() == 0) {
      Log.w(TAG, "tab 不能为null");
      return;
    }
    mTabs.clear();
    mTabs.addAll(tabs);
    removeAllViews();
    int i = 0;
    for (TabEntity entity : mTabs) {
      addView(createChild(i, entity));
      i++;
    }
    invalidate();
    requestFocus();
  }

  /**
   * 显示小红点
   */
  public void showRedSpot(int position, boolean show) {
    ((TabView) mViews.get(position)).showRedSpot(show);
  }

  private View createChild(int position, TabEntity tab) {
    TabView view = new TabView(getContext());
    //view.setOnFocusChangeListener(this);
    view.setTitle(tab.tabName);
    int size = ViewGroup.LayoutParams.WRAP_CONTENT;
    view.setLayoutParams(new LinearLayout.LayoutParams(size, size, 1));
    mViews.put(position, view);
    return view;
  }

  View lastFocusedView;

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
}
