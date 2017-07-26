package com.utstar.appstoreapplication.activity.windows.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;

/**
 * Created by lyy on 2016/9/5.
 * 消息对话框
 */
public class MsgDialog extends AbsDialog implements View.OnClickListener {
  String mMsgStr, mTitleStr;
  @Bind(R.id.title) TextView mTitle;
  @Bind(R.id.msg) TextView mMsg;
  @Bind(R.id.enter) Button mEnter;
  @Bind(R.id.cancel) Button mCancel;
  private OnMsgDialogCallback mCallback;
  private boolean showCancelBt = false;

  /**
   * 对话框按钮回调
   */
  public interface OnMsgDialogCallback {
    public void onEnter();

    public void onCancel();
  }

  /**
   * @param title 标题
   * @param msg 消息，如果需要某段字体高亮，请传入html标签文本
   * @param showCancelBt 是否显示取消按钮
   */
  public MsgDialog(Context context, String title, String msg, boolean showCancelBt) {
    super(context, R.style.blur_dialog);
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    mMsgStr = msg;
    mTitleStr = title;
    this.showCancelBt = showCancelBt;
    setCancelable(false);
    init();
  }

  private void init() {
    mTitle.setText(mTitleStr);
    mMsg.setText(Html.fromHtml(mMsgStr));
    mCancel.setVisibility(showCancelBt ? View.VISIBLE : View.GONE);
    mEnter.setOnClickListener(this);
    mCancel.setOnClickListener(this);
  }

  /**
   * 设置取消按钮默认被选中
   */
  public void requestCancelBtFocus() {
    if (mCancel != null) {
      mCancel.requestFocus();
    }
  }

  /**
   * 设置按钮回调
   */
  public void setDialogCallback(OnMsgDialogCallback msgDialogCallback) {
    mCallback = msgDialogCallback;
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_msg;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.enter:
        if (mCallback != null) {
          mCallback.onEnter();
        }
        break;
      case R.id.cancel:
        if (mCallback != null) {
          mCallback.onCancel();
        }
        break;
    }
    dismiss();
  }
}
