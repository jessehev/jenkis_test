package com.ut.wb.ui.metro;

import activity.appstoreapplication.utstar.com.ui.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ut.wb.ui.TagUtil;
import com.ut.wb.ui.Utils;
import com.ut.wb.ui.constance.TagConstance;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Aria.Lao on 2016/12/12.
 */
class BaseItemView extends RelativeLayout {
  protected MetroItemEntity.MetroBaseData mItemData;
  protected boolean isHasFocus = false;
  private int mRadius = (int) getResources().getDimension(R.dimen.dimen_10dp);
  protected int mType;

  public BaseItemView(Context context) {
    super(context);
  }

  public BaseItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BaseItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void bindData(int type, MetroItemEntity.MetroBaseData itemData) {
    mType = type;
    mItemData = itemData;
  }

  public void update(MetroItemEntity.MetroBaseData itemData) {

  }

  /**
   * 处理左上角标签
   * 0：热门、1：新增、2：普通
   */
  protected void handleHotTag(ImageView img, int tag) {
    TagUtil.handleHotTag(img, tag);
  }

  /**
   * 处理标签
   * 0：免费、1：普通、2：内购、3：收费、4：限时免费
   */
  protected void handleTag(ImageView img, int tag, boolean isBuy) {
    TagUtil.handleTag(img, tag, isBuy);
  }

  /**
   * 加载背景图
   */
  protected void loadImg(ImageView img, String url) {
    boolean isGif = Utils.getFileExtensionName(url).equals("gif");

    RequestManager manager = Glide.with(getContext());
    int placeImg = R.mipmap.place_def;
    switch (mType) {
      case MetroLayout.NORMAL_HORIZONTAL:
      case MetroLayout.SIMPLE_HORIZONTAL:
        placeImg = R.mipmap.place_horizontal;
      case MetroLayout.NORMAL_MAX_VERTICAL:
      case MetroLayout.NORMAL_VERTICAL:
      case MetroLayout.SIMPLE_VERTICAL:
        placeImg = R.mipmap.place_vertical;
        break;
      case MetroLayout.NORMAL_SQUARE:
      case MetroLayout.SIMPLE_SQUARE:
        placeImg = R.mipmap.place_def;
    }

    if (isGif) {
      manager.load(url)
          .diskCacheStrategy(DiskCacheStrategy.SOURCE)
          .placeholder(placeImg)
          .error(placeImg)
          .bitmapTransform(new RoundedCornersTransformation(getContext(), mRadius, 0,
              RoundedCornersTransformation.CornerType.ALL))
          .crossFade()
          .into(img);
    } else {
      Utils.setImg(getContext().getApplicationContext(), img, url, mRadius);
    }
  }

  protected void loadImg(ImageView img, int imgId) {
    //Utils.setImg(getContext().getApplicationContext(), img, imgId, mRadius);
    img.setBackgroundResource(imgId);
  }

  public void hasFocus(boolean isHasFocus) {
    this.isHasFocus = isHasFocus;
  }

  public <T extends View> T getView(int id) {
    return (T) findViewById(id);
  }
}
