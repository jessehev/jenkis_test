package com.ut.wb.ui;

import activity.appstoreapplication.utstar.com.ui.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Aria.Lao on 2016/12/19.
 * 自定义图标大小的EditText
 */
public class CustomIconEditText extends EditText {
  private int mIconW, mIconH;

  public CustomIconEditText(Context context) {
    this(context, null);
  }

  public CustomIconEditText(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.editTextStyle);
  }

  public CustomIconEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    TypedArray a = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.CustomIconEditText, defStyleAttr, 0);
    mIconW = (int) a.getDimension(R.styleable.CustomIconEditText_cie_icon_w, 0);
    mIconH = (int) a.getDimension(R.styleable.CustomIconEditText_cie_icon_h, 0);
    a.recycle();
  }

  @Override
  public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right,
      Drawable bottom) {
    if (left != null) left.setBounds(0, 0, mIconW, mIconH);
    if (top != null) top.setBounds(0, 0, mIconW, mIconH);
    if (right != null) right.setBounds(0, 0, mIconW, mIconH);
    if (bottom != null) bottom.setBounds(0, 0, mIconW, mIconH);
    super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
  }

  @Override
  public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
    if (left != null) left.setBounds(0, 0, mIconW, mIconH);
    if (top != null) top.setBounds(0, 0, mIconW, mIconH);
    if (right != null) right.setBounds(0, 0, mIconW, mIconH);
    if (bottom != null) bottom.setBounds(0, 0, mIconW, mIconH);
    super.setCompoundDrawables(left, top, right, bottom);
  }
}
