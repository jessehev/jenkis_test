package com.ut.wb.ui.metro;

import activity.appstoreapplication.utstar.com.ui.R;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by Aria.Lao on 2016/12/15. 简单的metro view
 */
final class SimpleMetroItemView extends BaseItemView {
  private static final String TAG = "SimpleMetroItemView";
  ImageView mImg, mTag, mHotTag;
  TextView mTitle;
  View mBg;
  MetroItemEntity.SimpleItemData mData;
  RoundedImageView mFloorImg;

  public SimpleMetroItemView(Context context) {
    super(context);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_metor_item_simple, this);
    mImg = getView(R.id.img);
    mBg = getView(R.id.bg);
    mTitle = getView(R.id.title);
    mFloorImg = getView(R.id.floor_img);
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

    if (itemData instanceof MetroItemEntity.SimpleItemData) {
      mData = (MetroItemEntity.SimpleItemData) itemData;
      if (!TextUtils.isEmpty(mData.title)) {
        mTitle.setText(mData.title);
      }
      if (!TextUtils.isEmpty(mData.floorImg)) {
        //loadImg(mFloorImg, mData.floorImg);
        Glide.with(getContext()).load(mData.floorImg).into(mFloorImg);
      }
      if (mData.resId != -1) {
        loadImg(mImg, mData.resId);
      } else {
        loadImg(mImg, itemData.img);
      }
      handleTag(mTag, mData.tag, mData.isBuy);
      handleHotTag(mHotTag, mData.hotTag);
    } else {
      Log.w(TAG, "数据结构错误");
    }
  }

  @Override public void hasFocus(boolean isHasFocus) {
    super.hasFocus(isHasFocus);
    mBg.setBackgroundResource(
        isHasFocus ? R.drawable.bg_metro_item_focused : R.drawable.bg_metro_item_def);
  }
}
