package com.utstar.appstoreapplication.activity.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.entity.net_entity.SbyEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import java.io.File;

/**
 * Created by Aria.Lao on 2016/12/26. 下载服务，只处理下载完成的情况
 */
public class DownloadServer extends Service {
  private static final String TAG = "DownloadServer";
  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    long len = 0;

    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      //可以通过intent获取到下载实体，下载实体中包含了各种下载状态
      DownloadEntity entity = intent.getParcelableExtra(Aria.ENTITY);
      switch (action) {
        case Aria.ACTION_PRE:  //预处理
          break;
        case Aria.ACTION_POST_PRE: //预处理完成
          //预处理完成，便可以获取文件的下载长度了
          len = entity.getFileSize();
          break;
        case Aria.ACTION_COMPLETE: //下载完成
          handleComplete(entity);
          break;
        case Aria.ACTION_FAIL:     // 下载失败
          L.w("下载地址【" + entity.getDownloadUrl() + "】的apk文件下载失败");
          if (entity.getDownloadPath().equals(DownloadHelpUtil.getHotUpdateResPath())) {
            FL.d(TAG, "tinker 下载失败");
            Aria.whit(getApplicationContext()).load(entity).start();
          }
          break;
        case Aria.ACTION_CANCEL:
          break;
      }
    }

    /**
     * 下载成功
     */
    private void handleComplete(DownloadEntity entity) {

      if (entity.getDownloadPath().equals(DownloadHelpUtil.getSBYApkPath())) {
        handleSbyInstall();
      } else if (entity.getDownloadPath().equals(DownloadHelpUtil.getHotUpdateResPath())) {
        handleHotUpdate(entity);
      } else if (entity.getDownloadPath().equals(DownloadHelpUtil.getWanBaApkPath())) {
        //玩吧升级在UpdateDialog中处理
      } else {
        DownloadHelpUtil.handleDownloadComplete(entity.getDownloadUrl());
      }
    }

    /**
     * 处理世博云平台安装
     */
    private void handleSbyInstall() {
      SbyEntity sbyEntity = SharePreUtil.getObject(KeyConstant.PRE_NAME, getApplicationContext(),
          KeyConstant.KEY_SBY_ENTITY, SbyEntity.class);
      if (sbyEntity != null) {
        final File apk = new File(DownloadHelpUtil.getSBYApkPath());
        if (apk.exists() && FileUtil.checkMD5(sbyEntity.md5, apk)) {
          ApkUtil.installApk(getApplicationContext(), apk.getPath());
          SharePreUtil.removeKey(KeyConstant.PRE_NAME, getApplicationContext(),
              KeyConstant.KEY_SBY_ENTITY);
        }
      }
    }

    /**
     * 处理热更新
     */
    private void handleHotUpdate(DownloadEntity entity) {
      String md5 = null;
      String hotResPath = DownloadHelpUtil.getHotUpdateResPath();
      try {
        md5 = SharePreUtil.getString(KeyConstant.PRE_NAME, BaseApp.context,
            KeyConstant.PRE.TEMP_TINKER_MD5);
        File path = new File(hotResPath);
        if (path.exists()) {
          if (!TextUtils.isEmpty(md5) && FileUtil.checkMD5(md5, path)) {
            TinkerInstaller.onReceiveUpgradePatch(BaseApp.context, hotResPath);
          } else {
            FL.d(TAG, "Tinker md5 错误，重新下载");
            Aria.whit(getApplicationContext()).load(entity).start();
          }
        } else {
          FL.d(TAG, "tinker path 不存在，重新下载");
          Aria.whit(getApplicationContext()).load(entity).start();
        }
      } catch (Exception e) {
        FL.d(TAG, "读取tinker md5 错误");
        FL.e(TAG, FL.getExceptionString(e));
      }
    }
  };

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme(getPackageName());
    filter.addAction(Aria.ACTION_PRE);
    filter.addAction(Aria.ACTION_POST_PRE);
    filter.addAction(Aria.ACTION_RESUME);
    filter.addAction(Aria.ACTION_START);
    filter.addAction(Aria.ACTION_RUNNING);
    filter.addAction(Aria.ACTION_STOP);
    filter.addAction(Aria.ACTION_CANCEL);
    filter.addAction(Aria.ACTION_COMPLETE);
    filter.addAction(Aria.ACTION_FAIL);
    registerReceiver(mReceiver, filter);
    Notification notification = new Notification();
    notification.flags = Notification.FLAG_ONGOING_EVENT;
    notification.flags |= Notification.FLAG_NO_CLEAR;
    notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
    startForeground(1, notification);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mReceiver);
  }
}
