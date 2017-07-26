package com.utstar.appstoreapplication.activity.windows.common;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.DialogLoadingBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;

/**
 * Created by “AriaLyy@outlook.com” on 2015/11/9.
 * 加载等待对话框
 */
@SuppressLint("ValidFragment") public class LoadingDialog extends BaseDialog<DialogLoadingBinding> {
  public LoadingDialog(boolean canCancel) {
    setCancelable(canCancel);
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    getDialog().getWindow()
        .setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_loading;
  }
}