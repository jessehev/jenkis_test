package com.utstar.appstoreapplication.activity.windows.activity.sign;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;

/**
 * Created by JesseHev on 2017-03-08.
 * 签到中奖填写用户信息的对话框
 */

public class SignHintDialog extends AbsDialog {

  @Bind(R.id.title) TextView mTitle;
  @Bind(R.id.content) TextView mContent;
  @Bind(R.id.number_edit) EditText mNumberEdit;
  @Bind(R.id.sure_btn) Button mSureBtn;

  public OnEnterClickLister mEnterClickLister;

  public interface OnEnterClickLister {
    /**
     * 确认按钮回调接口
     */
    void onEnterClick();

    /**
     * 返回回调
     */
    void onBackPressed();
  }

  /**
   * 防止生成多个对话框
   */
  private static volatile SignHintDialog INSTANCE = null;
  private static final Object LOCK = new Object();

  public static SignHintDialog getInstance(Context context, String content) {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new SignHintDialog(context, content);
      }
    }
    return INSTANCE;
  }

  public SignHintDialog(Context context) {
    super(context);
  }

  public SignHintDialog(Context context, String content) {
    super(context);
    setContent(content);
    initWindow();
  }

  /**
   * 获取编辑框内容
   */
  public String getEditText() {
    return mNumberEdit.getText().toString();
  }

  /**
   * 设置领奖信息
   */
  public void setContent(String content) {
    if (TextUtils.isEmpty(content)) {
      mContent.setText("");
    } else {
      mContent.setText(Html.fromHtml(content));
    }
  }

  public void setOnEnterClickLister(OnEnterClickLister enterClickLister) {
    mEnterClickLister = enterClickLister;
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_sign_hint_msg;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  @Override public void onBackPressed() {
    if (mEnterClickLister != null) {
      mEnterClickLister.onBackPressed();
    }
    //super.onBackPressed();
  }

  public void initWindow() {
    if (getWindow() != null) {
      WindowManager.LayoutParams params = getWindow().getAttributes();
      params.width = getDimen(R.dimen.dimen_900dp);
      params.height = getDimen(R.dimen.dimen_571dp);

      getWindow().setBackgroundDrawable(
          getContext().getResources().getDrawable(R.mipmap.bg_sign_hint_msg));
    }
  }

  public int getDimen(int dimenId) {
    return (int) getContext().getResources().getDimension(dimenId);
  }

  @OnClick(R.id.sure_btn) public void onClick() {
    if (mEnterClickLister != null) {
      mEnterClickLister.onEnterClick();
    }
  }
}
