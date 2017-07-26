package com.utstar.appstoreapplication.activity.windows.activity.day_task;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.windows.activity.sign.TipsDialog;

/**
 * Created by JesseHev on 2017/4/14.
 */

public class DayTaskMsgDialog extends AbsDialog {
  @Bind(R.id.content) TextView mContent;

  public DayTaskMsgDialog(Context context) {
    super(context, R.style.blur_dialog);
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_task_msg;
  }

  /**
   * 防止生成多个对话框
   */
  private static volatile DayTaskMsgDialog INSTANCE = null;
  private static final Object LOCK = new Object();

  public static DayTaskMsgDialog getInstance(Context context, String content) {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new DayTaskMsgDialog(context, content);
      }
    }
    return INSTANCE;
  }

  public DayTaskMsgDialog(Context context, String content) {
    super(context, R.style.blur_dialog);
    initWindow();
    setContent(content);
  }

  public void setContent(String content) {
    if (TextUtils.isEmpty(content)) {
      mContent.setText("");
    } else {
      mContent.setText(Html.fromHtml(content));
    }
  }

  @Override protected void dataCallback(int result, Object obj) {

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
  protected int getDimen(int dimenId) {
    return (int) getContext().getResources().getDimension(dimenId);
  }
  @OnClick(R.id.btn) public void onClick() {
    dismiss();
  }
}
