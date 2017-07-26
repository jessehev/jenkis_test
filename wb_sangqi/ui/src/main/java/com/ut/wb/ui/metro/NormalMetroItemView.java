package com.ut.wb.ui.metro;

import activity.appstoreapplication.utstar.com.ui.R;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ut.wb.ui.Utils;

/**
 * Created by Aria.Lao on 2016/12/15.
 * 默认的普通标签
 */
class NormalMetroItemView extends BaseItemView {
  private static final String TAG = "DefMetroItemView";

  TextView mTitle, mArea, mNum;
  ImageView mSb, mYkq, mPlay, mTag, mHotTag;
  ImageView mBackgroundImg;
  RelativeLayout mBottomBar;
  View mBg;
  MetroItemEntity.NormalItemData mData;

  public NormalMetroItemView(Context context) {
    super(context);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_metor_item_def, this);
    mTitle = getView(R.id.title);
    mArea = getView(R.id.area);
    mNum = getView(R.id.num);
    mBackgroundImg = getView(R.id.img);
    mSb = getView(R.id.sb);
    mYkq = getView(R.id.ykq);
    mBottomBar = getView(R.id.bottom_bar);
    mBg = getView(R.id.bg);
    mPlay = getView(R.id.play);
    mTag = getView(R.id.tag);
    mHotTag = getView(R.id.hot_tag);
  }

  @Override public void update(MetroItemEntity.MetroBaseData itemData) {
    super.update(itemData);
    bindData(mType, itemData);
  }

  @Override public void bindData(int type, MetroItemEntity.MetroBaseData itemData) {
    super.bindData(type, itemData);
    if (itemData == null) {
      return;
    }
    if (itemData instanceof MetroItemEntity.NormalItemData) {
      mData = (MetroItemEntity.NormalItemData) itemData;
      mTitle.setText(mData.title);
      mNum.setText(Utils.downloadCountHelp(mData.num));
      mArea.setText(mData.area);
      mArea.setVisibility(TextUtils.isEmpty(mData.area) ? GONE : VISIBLE);
      mSb.setVisibility(mData.isShowSb ? VISIBLE : INVISIBLE);
      mYkq.setVisibility(mData.isShowYkq ? VISIBLE : INVISIBLE);
      mPlay.setVisibility(mData.isShowPlay ? VISIBLE : INVISIBLE);
      loadImg(mBackgroundImg, itemData.img);
      handleTag(mTag, mData.tag, mData.isBuy);
      handleHotTag(mHotTag, mData.hotTag);
    } else {
      Log.w(TAG, "数据结构错误");
    }
  }

  @Override public void hasFocus(boolean isHasFocus) {
    super.hasFocus(isHasFocus);
    mBottomBar.setVisibility(isHasFocus ? VISIBLE : GONE);
    mTitle.setVisibility(isHasFocus ? GONE : VISIBLE);
    if (mData != null && mData.isShowPlay) {
      mPlay.setVisibility(isHasFocus ? VISIBLE : GONE);
    }
    mBg.setBackgroundResource(
        isHasFocus ? R.drawable.bg_metro_item_focused : R.drawable.bg_metro_item_def);
  }
}