package com.utstar.appstoreapplication.activity.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.Task;
import com.arialyy.frame.util.SharePreUtil;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.entity.net_entity.UpdateVersionEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;

/**
 * Created by AriaL on 2017/2/15. 热更新服务
 */
public class HotUpdateService extends Service {
  public static final String ENTITY_KEY = "ENTITY_KEY";
  public static final String CMD_STOP_SERVICE = "CMD_STOP_SERVICE";

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    Aria.whit(this).addSchedulerListener(new MyDownloadListener());
  }

  @Override public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    if (intent != null) {
      if (intent.getAction() != null && intent.getAction().equals(CMD_STOP_SERVICE)) {
        stopSelf();
        return;
      }
      UpdateVersionEntity entity = intent.getParcelableExtra(ENTITY_KEY);
      if (entity != null) {
        Aria.whit(this)
            .load(entity.apkUrl)
            .setDownloadPath(DownloadHelpUtil.getHotUpdateResPath())
            .setDownloadName("wanba.zip")
            .start();
      }
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Aria.whit(this).removeSchedulerListener();
  }

  private static class MyDownloadListener extends Aria.SimpleSchedulerListener {

    @Override public void onTaskRunning(Task task) {
      super.onTaskRunning(task);
    }

    @Override public void onTaskComplete(Task task) {
      super.onTaskComplete(task);
      TinkerInstaller.onReceiveUpgradePatch(BaseApp.context,
          DownloadHelpUtil.getHotUpdateResPath());
    }

    @Override public void onTaskFail(Task task) {
      super.onTaskFail(task);
      SharePreUtil.removeKey(KeyConstant.PRE_NAME, BaseApp.application, KeyConstant.PRE.TINKER_ID);
    }
  }
}
