package com.utstar.appstoreapplication.activity.windows.game_detail;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.aria.core.task.Task;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.DialogInstallBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.receiver.InstallReceiver;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;
import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Aria.Lao on 2017/5/27.
 * 即点即用加载界面
 */
@SuppressLint("ValidFragment") public class GameInstallDialog
    extends BaseDialog<DialogInstallBinding> {

  /**
   * 安装完成
   */
  private static final int COMPLETE = 0xab1;
  /**
   * 等待中
   */
  private static final int WATING = 0xab2;
  /**
   * 加载失败
   */
  private static final int FAIL = 0xab3;

  @Bind(R.id.hint) TextView mHint;
  @Bind(R.id.animation) ImageView mAnimation;

  private int mGameId;
  private GameDetailEntity mEntity;
  private static String DOWNLOAD_URL = "";
  private AnimationDrawable mAmDrawable;

  private Handler mHandler = new Handler() {
    int i = 0;

    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case FAIL:
          mHint.setText("加载失败，请重试");
          if (mAmDrawable.isRunning()) {
            mAmDrawable.stop();
          }
          mAnimation.setBackgroundResource(R.mipmap.icon_load_fail);
          break;
        case COMPLETE:
          mHint.setText("加载中，请耐心等待...  " + 100 + "%");
          ApkUtil.startGame(getContext(), String.valueOf(msg.obj));
          dismiss();
          break;
        case WATING:
          String spot = "";
          switch (i) {
            case 0:
              spot = "";
              break;
            case 1:
              spot = ".";
              break;
            case 2:
              spot = "..";
              break;
            case 3:
              spot = "...";
              break;
          }

          mHint.setText("加载中，请耐心等待" + spot + " " + msg.obj + "%");
          i++;
          if (i >= 3) {
            i = 0;
          }
          break;
      }
    }
  };

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      String packageName = intent.getStringExtra(InstallReceiver.DATA_PKG_NAME);
      if (!packageName.equals(mEntity.packageName)) return;
      if (InstallReceiver.ACTION_APK_INSTALL.equals(action)
          || InstallReceiver.ACTION_APK_UPDATE.equals(action)) {
        mHandler.obtainMessage(COMPLETE, packageName).sendToTarget();
      } else if (InstallReceiver.ACTION_APK_INSTALL_FAIL.equals(action)) {
        mHandler.obtainMessage(FAIL).sendToTarget();
      }
    }
  };

  /**
   * @param gameId 玩吧游戏ID
   */
  public GameInstallDialog(int gameId) {
    mGameId = gameId;
  }

  /**
   * @param entity 游戏详情实体
   */
  public GameInstallDialog(GameDetailEntity entity) {
    mEntity = entity;
  }

  private GameInstallDialog() {
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    //ImageManager.getInstance().loadBackground(mRootView, R.mipmap.bg_install_dialog);
    mRootView.setBackground(getResources().getDrawable(R.mipmap.bg_install_dialog));
    if (mEntity == null) {
      getModule(GameDetailModule.class).getGameDetailData(mGameId, 0);
    } else {
      DOWNLOAD_URL = mEntity.downloadUrl;
      if (AndroidUtils.isInstall(getContext(), mEntity.packageName)) {
        simWait();
      } else {
        startDownload();
      }
    }
    mAnimation.setBackgroundResource(R.drawable.animation_start);
    mAmDrawable = (AnimationDrawable) mAnimation.getBackground();
    mAmDrawable.start();
  }

  /**
   * 模拟等待
   */
  private void simWait() {
    CountDownTimer timer = new CountDownTimer(4000, 40) {
      int i = 0;

      @Override public void onTick(long millisUntilFinished) {
        mHandler.obtainMessage(WATING, i).sendToTarget();
        i++;
      }

      @Override public void onFinish() {
        mHandler.obtainMessage(COMPLETE, mEntity.packageName).sendToTarget();
      }
    };
    timer.start();
  }

  /**
   * 下载
   */
  private void startDownload() {
    if (mEntity != null && getModule(GameDetailModule.class).checkoutSpace(mEntity.apkSize)) {
      DownloadEntity downloadEntity = Aria.get(getContext()).getDownloadEntity(mEntity.downloadUrl);
      if (downloadEntity == null) {
        download();
      } else {
        if (downloadEntity.isDownloadComplete()) {
          File apk = new File(downloadEntity.getDownloadPath());
          if (apk.exists() && FileUtil.checkMD5(mEntity.apkMd5Code, apk)) {
            ApkUtil.installApk(getContext(), apk.getPath());
          } else {
            download();
          }
        } else {
          switch (downloadEntity.getState()) {
            case DownloadEntity.STATE_WAIT:
            case DownloadEntity.STATE_FAIL:
            case DownloadEntity.STATE_CANCEL:
            case DownloadEntity.STATE_STOP:
              break;
            case DownloadEntity.STATE_PRE:
            case DownloadEntity.STATE_POST_PRE:
            case DownloadEntity.STATE_DOWNLOAD_ING:
              break;
            case DownloadEntity.STATE_COMPLETE:
            case DownloadEntity.STATE_OTHER:
              File apk = new File(downloadEntity.getDownloadPath());
              if (apk.exists() && FileUtil.checkMD5(mEntity.apkMd5Code, apk)) {
                ApkUtil.installApk(getContext(), apk.getPath());
              }
              break;
          }
        }
      }
    }
  }

  private void download() {
    DownloadHelpUtil.createDownloadInfo(
        DownloadHelpUtil.getApkDownloadPath(mEntity.downloadUrl, mEntity.packageName), mEntity);
    Aria.whit(getContext())
        .load(mEntity.downloadUrl)
        .setDownloadPath(
            DownloadHelpUtil.getApkDownloadPath(mEntity.downloadUrl, mEntity.packageName))
        .setDownloadName(
            DownloadHelpUtil.getApkDownloadName(mEntity.downloadUrl, mEntity.packageName))
        .start();
  }

  @Override public void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme(getContext().getPackageName());
    filter.addAction(InstallReceiver.ACTION_APK_INSTALL);
    filter.addAction(InstallReceiver.ACTION_APK_UPDATE);
    getContext().registerReceiver(mReceiver, filter);
    Aria.whit(getContext()).addSchedulerListener(new MyDownloadListener(mHandler));
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mReceiver != null) {
      getContext().unregisterReceiver(mReceiver);
    }
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_install;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == GameDetailActivity.GAME_DETAIL_RESULT) {
      if (obj == null) {
        mHandler.obtainMessage(FAIL).sendToTarget();
      } else {
        mEntity = (GameDetailEntity) obj;
        DOWNLOAD_URL = mEntity.downloadUrl;
        if (AndroidUtils.isInstall(getContext(), mEntity.packageName)) {
          simWait();
        } else {
          startDownload();
        }
      }
    }
  }

  private static class MyDownloadListener extends Aria.SimpleSchedulerListener {
    WeakReference<Handler> handler;

    MyDownloadListener(Handler handler) {
      this.handler = new WeakReference<>(handler);
    }

    @Override public void onTaskRunning(Task task) {
      super.onTaskRunning(task);
      if (task.getDownloadEntity().getDownloadUrl().equals(DOWNLOAD_URL)) {
        int percent = (int) (task.getCurrentProgress() * 100 / task.getFileSize());
        handler.get().obtainMessage(WATING, percent).sendToTarget();
      }
    }

    @Override public void onTaskFail(Task task) {
      super.onTaskFail(task);
      if (task.getDownloadEntity().getDownloadUrl().equals(DOWNLOAD_URL)) {
        handler.get().obtainMessage(FAIL).sendToTarget();
      }
    }
  }
}
