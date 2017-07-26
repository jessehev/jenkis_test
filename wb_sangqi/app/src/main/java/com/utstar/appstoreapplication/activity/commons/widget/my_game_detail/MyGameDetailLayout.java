package com.utstar.appstoreapplication.activity.commons.widget.my_game_detail;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.utils.GameUpdateUtil;
import com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail.MyGameDetailActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aria.Lao on 2016/12/27.
 * 我的游戏详情View
 */
public class MyGameDetailLayout extends FrameLayout
    implements View.OnFocusChangeListener, View.OnClickListener,
    MyGameDetailItemView.onRemoveCallback {
  private static final String TAG = "MyGameDetailLayout";
  /**
   * 已有游戏
   */
  public static final int MY_GAME = 0xa1;
  /**
   * 下载中
   */
  public static final int DOWNLOADING = 0xa2;
  /**
   * 待更新
   */
  public static final int UPDATE = 0xa3;

  /**
   * mRowInterval：横向间隔，mColumnInterval：竖向间隔
   */
  private int mRowInterval = -1, mColumnInterval = -1;
  private int mChildWidth, mChildHeight;
  private List<MyGameDetailEntity> mData = new ArrayList<>();
  private int mType;
  private int[] rowOffset = new int[2];
  private SparseArray<MyGameDetailItemView> mViews = new SparseArray<>();
  private AnimatorSet mScaleAnimator;
  private OnItemClickListener mItemClickListener;
  private OnItemFocusChangeListener mItemFocusChange;
  private Map<String, Integer> mLinks = new HashMap<>();
  private boolean isShowCheckBox = false;
  private int mCurrentPosition = 0;

  /**
   * item点击事件
   */
  interface OnItemClickListener {
    public void onItemClick(View view, int position, MyGameDetailEntity entity);
  }

  /**
   * item选中事件
   */
  public interface OnItemFocusChangeListener {
    public void onItemFocusChange(View view, boolean hasFocus, int position,
        MyGameDetailEntity entity);
  }

  public MyGameDetailLayout(Context context) {
    this(context, null);
  }

  public MyGameDetailLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  //@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  //  mWidth = MeasureSpec.getSize(widthMeasureSpec);
  //  mHeight = MeasureSpec.getSize(heightMeasureSpec);
  //  mChildWidth = (mWidth - (mColumnInterval * 4)) / 5;
  //  mChildHeight = (mHeight - mRowInterval) / 2;
  //}

  private void init(Context context, AttributeSet attrs) {
    mRowInterval = (int) context.getResources().getDimension(R.dimen.dimen_30dp);
    //mColumnInterval = (int) context.getResources().getDimension(R.dimen.dimen_5dp);
    mColumnInterval = 0;
    mChildWidth = (int) getResources().getDimension(R.dimen.dimen_340dp);
    mChildHeight = (int) getResources().getDimension(R.dimen.dimen_400dp);
  }

  /**
   * 设置孩子数据，最多只能设置10条数据
   *
   * @param type {@link MY_GAME}、{@link DOWNLOADING}、{@link UPDATE}、
   */
  public void bindData(int type, List<MyGameDetailEntity> data) {
    if (data == null || data.size() == 0 || data.size() > 10) {
      L.d(TAG, "data 不能为 null, 并且需要 0 < size < 10");
      return;
    }
    mType = type;
    mData.clear();
    mData.addAll(data);
    createChild();
  }

  @Override public void onRemove(View view) {

  }

  /**
   * 请求item的焦点
   */
  public void requestItemFocus(int position) {
    if (position < 0 || position > mData.size() - 1) {
      L.w("position 错误");
      return;
    }
    mViews.get(position).requestFocus();
  }

  /**
   * 获取数据
   */
  public List<MyGameDetailEntity> getData() {
    return mData;
  }

  /**
   * 更新状态
   */
  public synchronized void updateState(DownloadEntity entity) {
    String url = "";
    if (mLinks == null || mLinks.size() == 0 || entity == null) {
      return;
    }
    url = entity.getDownloadUrl();
    if (TextUtils.isEmpty(url)) return;
    int position = mLinks.get(url);
    mViews.get(position).updateState(entity.getState());
  }

  /**
   * 更新状态
   */
  public synchronized void updateState(String downloadUrl, int state) {
    if (mLinks == null || mLinks.size() == 0) return;
    Integer position = mLinks.get(downloadUrl);
    if (position != null) {
      mViews.get(position).updateState(state);
    }
  }

  /**
   * 通过下载实体更新进度条
   */
  public synchronized void updateProgress(DownloadEntity entity) {
    if (mLinks == null || mLinks.size() == 0) return;
    Integer position = mLinks.get(entity.getDownloadUrl());
    if (position != null) {
      mViews.get(position).updateProgress(entity.getCurrentProgress());
    }
  }

  /**
   * 处理删除
   */
  public synchronized void handleRemove() {
    List<Integer> positions = getCheckedPosition();
    List<MyGameDetailEntity> back = new ArrayList<>();
    boolean createViewNow = false;
    if (positions.size() == 0) return;
    for (int i = 0, len = positions.size(); i < len; i++) {
      Integer position = positions.get(i);
      if (position < 0 || position >= mData.size()) break;
      MyGameDetailEntity entity = mData.get(position);
      if (entity.isAddPackage) {
        createViewNow = true;
        //back.add(mData.get(position));
      } else if (mType != UPDATE) {
        MyGameDetailItemView itemView = mViews.get(position);
        back.add(mData.get(position));
        itemView.handleRemove();
      }
    }
    if (mType == DOWNLOADING) {
      MyGameDetailActivity activity = ((MyGameDetailActivity) getContext());
      for (MyGameDetailEntity entity : back) {
        mData.remove(entity);
        activity.downTotalSize();
        handleActivityUpdate(true, entity);
      }
    }
  }

  private synchronized void handleActivityUpdate(boolean isUnInstall, MyGameDetailEntity entity) {
    new Handler().postDelayed(() -> {
      MyGameDetailActivity activity = (MyGameDetailActivity) getContext();
      int totalSize = activity.getTotalSize();
      if (totalSize <= 0) {
        activity.finish();
        return;
      }
      activity.update(isUnInstall, entity);
    }, 200);
  }

  /**
   * 通过包名删除item
   */
  public synchronized void removeItemByPackageName(String packageName) {
    int position = getPosition(packageName);
    if (position < 0 || position >= mData.size()) return;
    MyGameDetailEntity entity = mData.get(position);
    mData.remove(position);
    handleActivityUpdate(true, entity);
  }

  /**
   * 通过包名删除item
   */
  public synchronized void removeItemByPackageName(boolean unInstall, String packageName) {
    int position = getPosition(packageName);
    if (position < 0 || position >= mData.size()) return;
    MyGameDetailEntity entity = mData.get(position);
    mData.remove(entity);
    handleActivityUpdate(unInstall, entity);
  }

  /**
   * 通过下载地址删除item
   */
  public void removeItemByUrl(String downloadUrl) {
    if (mLinks.get(downloadUrl) != null) {
      int position = mLinks.get(downloadUrl);
      if (position < 0 || position >= mData.size()) return;
      MyGameDetailEntity entity = mData.get(position);
      mData.remove(entity);
      handleActivityUpdate(true, entity);
    }
  }

  /**
   * 获取被选中的item的坐标
   */
  public List<Integer> getCheckedPosition() {
    List<Integer> positions = new ArrayList<>();
    for (int i = 0, len = getChildCount(); i < len; i++) {
      MyGameDetailItemView item = mViews.get(i);
      if (item.isChecked()) positions.add(i);
    }
    return positions;
  }

  /**
   * 通过游戏Id获取position
   */
  public int getPosition(int gameId) {
    int position = 0;
    for (MyGameDetailEntity entity : mData) {
      if (entity.gameId == gameId) {
        return position;
      }
      position++;
    }
    return -1;
  }

  /**
   * 通过包名获取Position
   */
  public synchronized int getPosition(String packageName) {
    int position = 0;
    for (MyGameDetailEntity entity : mData) {
      if (!entity.isAddPackage
          && !TextUtils.isEmpty(entity.packageName)
          && entity.packageName.equals(packageName)) {
        return position;
      }
      position++;
    }
    return -1;
  }

  private synchronized void createChild() {
    removeAllViews();
    rowOffset[0] = 0;
    rowOffset[1] = 0;
    mLinks.clear();
    int i = 0;
    for (MyGameDetailEntity entity : mData) {
      createChild(i, entity);
      if (!TextUtils.isEmpty(entity.downloadUrl)) {
        mLinks.put(entity.downloadUrl, i);
      }
      i++;
    }

    invalidate();
  }

  public boolean isShowCheckBox() {
    return isShowCheckBox;
  }

  /**
   * 显示CheckBox
   */
  public void showCheckBox(boolean isShow) {
    isShowCheckBox = isShow;
    for (int i = 0, len = getChildCount(); i < len; i++) {
      View view = mViews.get(i);
      if (view != null) {
        ((MyGameDetailItemView) view).showCheckBt(isShow);
        view.invalidate();
      }
    }
  }

  /**
   * 更新进度条
   */
  public void updateProgress(int position, long progress) {
    MyGameDetailItemView item = mViews.get(position);
    item.updateProgress(progress);
  }

  /**
   * update Item 数据
   */
  public void updateItemData(int position, MyGameDetailEntity entity) {
    MyGameDetailItemView item = mViews.get(position);
    item.bindData(entity);
  }

  /**
   * 设置Item事件
   */
  public void setOnItemClickListener(OnItemClickListener listener) {
    mItemClickListener = listener;
  }

  /**
   * 设置item foucs事件
   */
  public void setOnItemFocusChange(OnItemFocusChangeListener listener) {
    mItemFocusChange = listener;
  }

  /**
   * 创建item
   */
  private MyGameDetailItemView createChild(int i, MyGameDetailEntity entity) {
    MyGameDetailItemView child = null;
    LayoutParams flp = null;
    flp = new LayoutParams(mChildWidth, mChildHeight);
    child = new MyGameDetailItemView(getContext(), mType);
    if (i < 5) {
      flp.leftMargin = rowOffset[0];
      rowOffset[0] += mColumnInterval + mChildWidth;
    } else {
      flp.leftMargin = rowOffset[1];
      flp.topMargin = mRowInterval + mChildHeight;
      rowOffset[1] += mColumnInterval + mChildWidth;
    }
    child.setFocusable(true);
    child.setOnFocusChangeListener(this);
    child.setOnClickListener(this);
    child.setOnRemoveCallback(this);
    child.bindData(entity);
    addView(child, flp);
    mViews.append(i, child);
    return child;
  }

  public int getCurrentPosition() {
    return mCurrentPosition;
  }

  @Override public void onClick(View v) {
    int position = mViews.indexOfValue((MyGameDetailItemView) v);
    if (mItemClickListener != null) {
      if (position == -1) {
        Log.d(TAG, "没有找到被点击的View");
        return;
      }
      mItemClickListener.onItemClick(v, position, mData.get(position));
    }
    if (position != -1) {
      MyGameDetailItemView itemView = (MyGameDetailItemView) v;
      if (mViews.get(position).getType() == UPDATE) {
        itemView.handleClick();
        removeItemByPackageName(true, mData.get(position).packageName);
      } else {
        itemView.handleClick();
      }
    }
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
    if (v instanceof MyGameDetailItemView) {
      ((MyGameDetailItemView) v).hasFocus(hasFocus);
      if (hasFocus) {
        lastFocusedView = v;
        bringChildToFront(v);
        invalidate();
        ObjectAnimator animX =
            ObjectAnimator.ofFloat(v, "ScaleX", new float[] { 1.0F, 1.1F }).setDuration(200);
        ObjectAnimator animY =
            ObjectAnimator.ofFloat(v, "ScaleY", new float[] { 1.0F, 1.1F }).setDuration(200);
        mScaleAnimator = new AnimatorSet();
        mScaleAnimator.play(animX).with(animY);
        mScaleAnimator.start();
      } else {
        v.setScaleX(1.0f);
        v.setScaleY(1.0f);
      }
      int position = mViews.indexOfValue((MyGameDetailItemView) v);
      if (mItemFocusChange != null && position != -1 && position < mData.size()) {
        mCurrentPosition = position;
        mItemFocusChange.onItemFocusChange(v, hasFocus, position, mData.get(position));
      }
    }
  }
}
