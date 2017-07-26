package com.utstar.appstoreapplication.activity.windows.activity.discount_package;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.arialyy.frame.core.AbsDialog;
import com.arialyy.frame.util.StringUtil;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.manager.AnimManager;

/**
 * Created by JesseHev on 2017/4/14.
 */

public class GamePackageMsgDialog extends AbsDialog {
  @Bind(R.id.content) TextView mContent;
  @Bind(R.id.msg) TextView mMsg;
  @Bind(R.id.btnSure) Button mBtnSure;

  public GamePackageMsgDialog(Context context) {
    super(context, R.style.blur_dialog);
  }

  private GamePackageMsgOnclick mOnclick;

  interface GamePackageMsgOnclick {
    void sure();
  }

  public void setGamePackageMsgDialog(GamePackageMsgOnclick onclick) {
    mOnclick = onclick;
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_ice_msg;
  }

  public GamePackageMsgDialog(Context context, String packageName) {
    super(context, R.style.blur_dialog);
    initWindow();
    if (!TextUtils.isEmpty(packageName)) {
      mContent.setText(
          String.format(context.getResources().getString(R.string.packge_ice_break), packageName));
    }

    mMsg.setText(StringUtil.highlightKeyword(mMsg.getText().toString(), "温馨提示：",
        Color.parseColor("#ffea00")));
    mBtnSure.requestFocus();
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  public void initWindow() {
    if (getWindow() != null) {
      WindowManager.LayoutParams params = getWindow().getAttributes();

      params.dimAmount = 0.8f;
      getWindow().setAttributes(params);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

      params.width = getDimen(R.dimen.dimen_940dp);
      params.height = getDimen(R.dimen.dimen_610dp);
      getWindow().setBackgroundDrawable(
          getContext().getResources().getDrawable(R.mipmap.bg_sign_hint_msg));
    }
  }

  protected int getDimen(int dimenId) {
    return (int) getContext().getResources().getDimension(dimenId);
  }

  @OnFocusChange({ R.id.btnCancle, R.id.btnSure })
  public void onBtnFocusChange(View view, boolean focused) {
    if (focused) {
      AnimManager.getInstance().enlarge(view, 1.15f);
    } else {
      AnimManager.getInstance().narrow(view, 1.0f);
    }
  }

  @OnClick({ R.id.btnCancle, R.id.btnSure }) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btnCancle:
        break;
      case R.id.btnSure:
        if (mOnclick != null) {
          mOnclick.sure();
        }
        break;
    }
    dismiss();
  }
}
