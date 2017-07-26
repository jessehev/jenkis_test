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
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.R;

/**
 * Created by JesseHev on 2017-03-08.
 * 中奖填写用户信息的对话框
 */

public class TipsDialog extends AbsDialog {

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

    ///**
    // * 返回回调
    // */
    //void onBackPressed();
  }

  /**
   * 防止生成多个对话框
   */
  private static volatile TipsDialog INSTANCE = null;
  private static final Object LOCK = new Object();

  public static TipsDialog getInstance(Context context, String content) {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new TipsDialog(context, content);
      }
    }
    return INSTANCE;
  }

  public TipsDialog(Context context) {
    super(context);
  }

  public TipsDialog(Context context, String content) {
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

  //@Override public void onBackPressed() {
  //  if (mEnterClickLister != null) {
  //    //mEnterClickLister.onBackPressed();
  //    mEnterClickLister.onEnterClick();
  //  }
  //  //super.onBackPressed();
  //}

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
    if (mEnterClickLister != null && verify(getEditText())) {
      mEnterClickLister.onEnterClick();
    }
  }

  public boolean verify(String phone) {
    // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
    String telRegex = "[1][3578]\\d{9}";
    if (TextUtils.isEmpty(phone)) {
      showToastRes(R.string.hint_error);
      return false;
    } else if (phone.matches(telRegex)) {
      return true;
    } else {
      showToastRes(R.string.hint_error);
      return false;
    }
  }

  /**
   * show Toast
   */
  private void showToastRes(int resId) {
    T.showShort(getContext(), getContext().getResources().getString(resId));
  }
}
