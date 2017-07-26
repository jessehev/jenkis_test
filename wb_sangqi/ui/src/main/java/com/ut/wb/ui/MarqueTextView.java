package com.ut.wb.ui;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueTextView extends TextView {

  public MarqueTextView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setEllipsize(null);
    setSingleLine();
  }

  public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public MarqueTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public void startMarquee() {
    setEllipsize(TruncateAt.MARQUEE);
  }

  public void stopMarquee() {
    setEllipsize(null);
  }

  @Override public boolean isFocused() {
    return true;
  }
}
