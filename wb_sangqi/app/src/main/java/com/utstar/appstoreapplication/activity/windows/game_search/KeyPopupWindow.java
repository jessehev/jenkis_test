package com.utstar.appstoreapplication.activity.windows.game_search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.windows.base.BasePopupWindow;

/**
 * Created by Aria.Lao on 2016/12/19. 搜索按键详情框
 */
final class KeyPopupWindow extends BasePopupWindow {
  static final int KEY_POPUP_WINDOW_RESULT = 0xaf1;
  @Bind(R.id.left) TextView mLeft;
  @Bind(R.id.center) TextView mCenter;
  @Bind(R.id.right) TextView mRight;
  @Bind(R.id.top) TextView mTop;
  @Bind(R.id.bottom) TextView mBottom;
  private SearchKeyEntity mEntity;
  private View mItemView;

  public KeyPopupWindow(View item, Context context, Object obj, SearchKeyEntity entity) {
    super(context, new ColorDrawable(Color.parseColor("#20000000")), obj);
    mItemView = item;
    mEntity = entity;
    initWidget();
  }

  private void initWidget() {
    mItemView.setVisibility(View.INVISIBLE);
    int size = (int) getContext().getResources().getDimension(R.dimen.dimen_150dp);
    setWidth(size);
    setHeight(size);

    if (mEntity != null && mEntity.key != null && mEntity.key.length > 0) {
      mTop.setText(String.valueOf(mEntity.num));
      mLeft.setText(String.valueOf(mEntity.key[0]));
      mCenter.setText(String.valueOf(mEntity.key[1]));
      mRight.setText(String.valueOf(mEntity.key[2]));
      if (mEntity.key.length == 4) {
        mBottom.setFocusable(true);
        mBottom.setEnabled(true);
        mBottom.setText(String.valueOf(mEntity.key[3]));
      } else {
        mBottom.setFocusable(false);
        mBottom.setEnabled(false);
      }
      mCenter.requestFocus();
    }

    mCenter.setOnKeyListener((v, keyCode, event) -> {
      if (BuildConfig.DEBUG) {
        T.showLong(getContext(), "keyCode ==> " + keyCode);
      }
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        String key = "";
        switch (keyCode) {
          case KeyEvent.KEYCODE_DPAD_UP:
            key = mEntity.num + "";
            break;
          case KeyEvent.KEYCODE_DPAD_DOWN:
            if (mEntity.key.length == 4) {
              key = String.valueOf(mEntity.key[3]);
            } else {
              return false;
            }
            break;
          case KeyEvent.KEYCODE_DPAD_LEFT:
            key = String.valueOf(mEntity.key[0]);
            break;
          case KeyEvent.KEYCODE_DPAD_RIGHT:
            key = String.valueOf(mEntity.key[2]);
            break;
          case KeyEvent.KEYCODE_ENTER:
          case KeyEvent.KEYCODE_DPAD_CENTER:
            key = String.valueOf(mEntity.key[1]);
            break;
        }
        getSimplerModule().onDialog(KEY_POPUP_WINDOW_RESULT, key);
        dismiss();
      }

      return false;
    });
  }

  @OnClick({ R.id.left, R.id.center, R.id.right, R.id.top, R.id.bottom })
  public void onClick(View view) {
    String key = "";
    switch (view.getId()) {
      case R.id.left:
        key = String.valueOf(mEntity.key[0]);
        break;
      case R.id.right:
        key = String.valueOf(mEntity.key[2]);
        break;
      case R.id.center:
        key = String.valueOf(mEntity.key[1]);
        break;
      case R.id.top:
        key = mEntity.num + "";
        break;
      case R.id.bottom:
        key = String.valueOf(mEntity.key[3]);
        break;
    }
    getSimplerModule().onDialog(KEY_POPUP_WINDOW_RESULT, key);
    dismiss();
  }

  @Override public void dismiss() {
    mItemView.setVisibility(View.VISIBLE);
    mItemView.requestFocus();
    super.dismiss();
  }

  @Override protected int setLayoutId() {
    return R.layout.pop_search_key;
  }
}
