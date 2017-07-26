package com.utstar.appstoreapplication.activity.windows.launcher;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.Task;
import com.arialyy.frame.core.AbsFrame;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.databinding.DialogUpdateBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.UpdateVersionEntity;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;
import com.wanba.util.IRestartWanba;
import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Aria.Lao on 2017/1/3.
 * 玩吧更新对话框
 */
@SuppressLint("ValidFragment") public class UpdateDialog extends BaseDialog<DialogUpdateBinding> {
  private static final int RUNNING = 0x0a2;
  private static final int COMPLETE = 0x0a3;

  @Bind(R.id.progressbar) ProgressBar mPb;
  @Bind(R.id.hint) TextView mHint;
  @Bind(R.id.bg) View mView;

  UpdateVersionEntity mEntity;
  private String DOWNLOAD_PATH;

  private Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      Task task = (Task) msg.obj;
      switch (msg.what) {
        case RUNNING:
          int percent = (int) (task.getCurrentProgress() * 100 / task.getFileSize());
          //mPb.setProgress(percent);
          mHint.setText("平台正在更新，请耐心等待...  " + percent + "%");
          break;
        case COMPLETE:
          //mPb.setProgress(100);
          File apk = new File(DOWNLOAD_PATH);
          if (apk.exists() && FileUtil.checkMD5(mEntity.md5, apk)) {
            mHint.setText("平台正在更新，请耐心等待..." + 100 + "%");
            ApkUtil.installWanba(getContext(), apk.getPath());
          }
          break;
      }
    }
  };

  public UpdateDialog() {

  }

  public UpdateDialog(UpdateVersionEntity entity) {

    mEntity = entity;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setStyle(STYLE_NO_TITLE, R.style.UpdateDialog);
    setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_update;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    if (mEntity.updateType == 2) { //强制升级
      setCancelable(false);
    } else {
      setCancelable(true);
    }
    DOWNLOAD_PATH = DownloadHelpUtil.getWanBaApkPath();
    File apk = new File(DOWNLOAD_PATH);
    if (apk.exists() && FileUtil.checkMD5(mEntity.md5, apk)) {
      mHint.setText("平台正在更新，请耐心等待..." + 100 + "%");
      ApkUtil.installWanba(getContext(), apk.getPath());
    } else {
      Aria.whit(getContext())
          .load(mEntity.apkUrl)
          .setDownloadPath(DOWNLOAD_PATH)
          .setDownloadName(DownloadHelpUtil.WANBA_APK_NAME)
          .start();
    }
    getDialog().setOnKeyListener((dialog, keyCode, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          if (onDoubleClickExit()) {
            AbsFrame.getInstance().exitApp(false);
          }
          return true;
        }
      }
      return false;
    });
  }

  private long mFirstClickTime = 0;

  /**
   * 双击退出
   */
  private boolean onDoubleClickExit(long timeSpace) {
    long currentTimeMillis = System.currentTimeMillis();
    if (currentTimeMillis - mFirstClickTime > timeSpace) {
      T.showShort(getContext(), "再按一次退出");
      mFirstClickTime = currentTimeMillis;
      return false;
    } else {
      return true;
    }
  }

  /**
   * 双击退出，间隔时间为2000ms
   */
  public boolean onDoubleClickExit() {
    return onDoubleClickExit(2000);
  }

  @Override public void onResume() {
    super.onResume();
    Aria.whit(getContext()).addSchedulerListener(new MyDownloadListener(mHandler));
  }

  private static class MyDownloadListener extends Aria.SimpleSchedulerListener {
    WeakReference<Handler> handler;

    MyDownloadListener(Handler handler) {
      this.handler = new WeakReference<>(handler);
    }

    @Override public void onTaskRunning(Task task) {
      super.onTaskRunning(task);
      handler.get().obtainMessage(RUNNING, task).sendToTarget();
    }

    @Override public void onTaskComplete(Task task) {
      super.onTaskComplete(task);
      handler.get().obtainMessage(COMPLETE, task).sendToTarget();
    }
  }
}
