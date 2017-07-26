package com.ut.wb.ui.TabLayout;

import activity.appstoreapplication.utstar.com.ui.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Aria.Lao on 2016/12/9.
 * tablayout 每一个子项
 */
class TabView extends LinearLayout {

  private TextView mTitle;
  private ImageView mImg;
  private View mRedSpot;
  private boolean isSelected = false;

  public TabView(Context context) {
    this(context, null);
  }

  public TabView(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override public boolean isSelected() {
    return isSelected;
  }

  public ImageView getBar() {
    return mImg;
  }

  public void setSelected(boolean isSelected, boolean useAnim) {
    this.isSelected = isSelected;
    if (isSelected) {
      mTitle.setTextColor(Color.WHITE);
      if (useAnim) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mTitle, "ScaleX", 1.0f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTitle, "ScaleY", 1.0f, 1.2f);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);
        set.setDuration(200).start();
      }
      mImg.setVisibility(VISIBLE);
    } else {
      mTitle.setTextColor(Color.GRAY);
      if (useAnim) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mTitle, "ScaleX", 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mTitle, "ScaleY", 1.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);
        set.setDuration(200).start();
      }
      mImg.setVisibility(GONE);
    }
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_main_tab, this);
    mTitle = (TextView) findViewById(R.id.title);
    mImg = (ImageView) findViewById(R.id.img);
    mRedSpot = findViewById(R.id.red_spot);
  }

  public void showRedSpot(boolean show) {
    mRedSpot.setVisibility(show ? VISIBLE : GONE);
  }

  public void setTitle(CharSequence title) {
    mTitle.setText(title);
  }
}
