package com.utstar.appstoreapplication.activity.windows.launcher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.databinding.DialogHotUpdateBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.UpdateVersionEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.tinker.service.TinkerResultService;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;
import java.io.File;
import java.lang.ref.WeakReference;

import static com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil.HOT_UPDATE_FILE_NAME;

/**
 * Created by Aria.Lao on 2017/1/9.
 * 热更新对话框
 */
@SuppressLint("ValidFragment") public class HotUpdateDialog
    extends BaseDialog<DialogHotUpdateBinding> {
  private static final String TAG = "HotUpdateDialog";
  private static final int DOWNLOAD_RUNNING = 0x0a2;
  private static final int DOWNLOAD_COMPLETE = 0x0a3;
  private static final int UPDATE_SUCCESS = 0x0a4;
  private static final int UPDATE_RUNNING = 0x0a5;

  @Bind(R.id.pb) ProgressBar mPb;
  @Bind(R.id.progress) TextView mProgress;

  UpdateVersionEntity mEntity;
  private String DOWNLOAD_PATH;
  private OnDismissListener mListener;
  private boolean isHintProgress = false;

  public interface OnDismissListener {
    public void onDismiss();
  }

  private Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      Task task = null;
      if (msg.obj instanceof Task) {
        task = (Task) msg.obj;
      }
      switch (msg.what) {
        case DOWNLOAD_RUNNING:
          if (task == null) break;
          int percent = (int) (task.getCurrentProgress() * 100 / task.getFileSize());
          updatePb(true, percent);
          break;
        case DOWNLOAD_COMPLETE:
          updatePb(true, 100);
          //安装操作在Service处理
          //TinkerInstaller.onReceiveUpgradePatch(BaseApp.context, DOWNLOAD_PATH);
          simulatorProgress();
          break;
        case UPDATE_SUCCESS:
          updatePb(false, 100);
          T.showShort(getContext(), "平台更新完成，即将重启");
          new Handler().postDelayed(() -> AndroidUtils.reStartApp(BaseApp.application), 2000);
          break;
        case UPDATE_RUNNING:
          updatePb(false, msg.arg1);
          break;
      }
    }

    private void updatePb(boolean isDownload, int percent) {
      if (isHintProgress) return;
      int _percent;
      if (isDownload) {
        _percent = percent == 100 ? 10 : percent / 10;
        if (_percent > 10) _percent = 10;
      } else {
        _percent = 10 + percent;
        if (percent == 100) {
          _percent = 100;
        }
      }

      //mPb.setProgress(_percent);
      mProgress.setText("平台正在更新，请耐心等待...  " + _percent + "%");
    }
  };

  /**
   * 模拟进度
   */
  CountDownTimer timer;

  private void simulatorProgress() {
    timer = new CountDownTimer(160 * 1000, 1000) {
      int i = 0;

      @Override public void onTick(long millisUntilFinished) {
        mHandler.obtainMessage(UPDATE_RUNNING, i / 2, 0).sendToTarget();
        i++;
      }

      @Override public void onFinish() {
        mHandler.obtainMessage(UPDATE_RUNNING, 80, 0).sendToTarget();
      }
    };
    timer.start();
  }

  BroadcastReceiver mCompleteReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (!TextUtils.isEmpty(action)) {
        if (action.equals(TinkerResultService.UPDATE_SUCCESS)) {
          if (timer != null) {
            timer.cancel();
          }
          mHandler.obtainMessage(UPDATE_SUCCESS).sendToTarget();
        } else if (action.equals(TinkerResultService.UPDATE_FAIL)) {
          T.showShort(getContext(), "平台更新失败，重新更新");
          check();
          //mPb.setProgress(0);
          if (isHintProgress) return;
          mProgress.setText("平台正在更新，请耐心等待...  0%");
        }
      }
    }
  };

  public void setOnDismissListener(OnDismissListener listener) {
    mListener = listener;
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_hot_update;
  }

  public HotUpdateDialog() {

  }

  public HotUpdateDialog(boolean hintProgress, UpdateVersionEntity entity) {
    isHintProgress = hintProgress;
    //mProgress.setVisibility(isHintProgress ? View.GONE : View.VISIBLE);
    mEntity = entity;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    DOWNLOAD_PATH = DownloadHelpUtil.getHotUpdateResPath();
    mProgress.setVisibility(isHintProgress ? View.GONE : View.VISIBLE);
    check();
    //simulatorProgress();
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
   * 双击退出，间隔时间为2000ms
   */
  public boolean onDoubleClickExit() {
    return onDoubleClickExit(2000);
  }

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

  private void check() {
    File path = new File(DOWNLOAD_PATH);
    String oldTinkerId =
        SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TINKER_ID);
    if (TextUtils.isEmpty(oldTinkerId) || Integer.parseInt(oldTinkerId) < Integer.parseInt(
        mEntity.hotUpdateVersionCode)) {
      if (path.exists()) {
        if (FileUtil.checkMD5(mEntity.hotUpdateMd5, path)) {
          TinkerInstaller.onReceiveUpgradePatch(BaseApp.context,
              DownloadHelpUtil.getHotUpdateResPath());
        } else {
          downloadHotPath(mEntity);
        }
      } else {
        downloadHotPath(mEntity);
      }
    }
  }

  private void downloadHotPath(UpdateVersionEntity entity) {
    if (entity == null || TextUtils.isEmpty(entity.hotUpdateVersionCode) || TextUtils.isEmpty(
        entity.hotUpdateMd5)) {
      return;
    }
    SharePreUtil.putString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TEMP_TINKER_ID,
        entity.hotUpdateVersionCode);
    SharePreUtil.putString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TEMP_TINKER_MD5,
        entity.hotUpdateMd5);
    Aria.whit(getContext())
        .load(mEntity.apkUrl)
        .setDownloadPath(DOWNLOAD_PATH)
        .setDownloadName(HOT_UPDATE_FILE_NAME)
        .start();
  }

  @Override public void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter();
    filter.addAction(TinkerResultService.UPDATE_SUCCESS);
    filter.addAction(TinkerResultService.UPDATE_FAIL);
    getContext().registerReceiver(mCompleteReceiver, filter);
    Aria.whit(getContext()).addSchedulerListener(new HotUpdateDialog.MyDownloadListener(mHandler));
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mCompleteReceiver != null) {
      getContext().unregisterReceiver(mCompleteReceiver);
    }
  }

  @Override public void dismiss() {
    super.dismiss();
    if (mListener != null) {
      mListener.onDismiss();
    }
  }

  private static class MyDownloadListener extends Aria.SimpleSchedulerListener {
    WeakReference<Handler> handler;

    MyDownloadListener(Handler handler) {
      this.handler = new WeakReference<>(handler);
    }

    @Override public void onTaskRunning(Task task) {
      super.onTaskRunning(task);
      handler.get().obtainMessage(DOWNLOAD_RUNNING, task).sendToTarget();
    }

    @Override public void onTaskComplete(Task task) {
      super.onTaskComplete(task);
      handler.get().obtainMessage(DOWNLOAD_COMPLETE, task).sendToTarget();
    }

    @Override public void onTaskFail(Task task) {
      super.onTaskFail(task);
      SharePreUtil.removeKey(KeyConstant.PRE_NAME, BaseApp.application, KeyConstant.PRE.TINKER_ID);
    }
  }
}

