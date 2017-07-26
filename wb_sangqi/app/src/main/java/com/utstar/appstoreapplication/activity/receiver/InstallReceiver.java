package com.utstar.appstoreapplication.activity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.utils.ActionUtil;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;

/**
 * Created by Aria.Lao on 2016/12/26.
 * 安装卸载广播接收器
 */
public class InstallReceiver extends BroadcastReceiver {
  private final String TAG = "InstallReceiver";

  /**
   * apk安装
   */
  public static final String ACTION_APK_INSTALL = "WB_ACTION_APK_INSTALL";

  /**
   * apk卸载
   */
  public static final String ACTION_APK_UNINSTALL = "WB_ACTION_APK_UNINSTALL";

  /**
   * APK更新
   */
  public static final String ACTION_APK_UPDATE = "WB_ACTION_UPDATE";

  /**
   * 安装失败，该Action只用于静默安装
   */
  public static final String ACTION_APK_INSTALL_FAIL = "WB_ACTION_FAIL";

  /**
   * 包名
   */
  public static final String DATA_PKG_NAME = "WB_DATA_PKG_NAME";

  @Override public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    String packageName = intent.getData().getSchemeSpecificPart();
    if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
      handleInstall(context, packageName);
    } else if (Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action)) {
      handleUnInstall(context, packageName);
    } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
      handleUpdate(context, packageName);
    }
  }

  /**
   * 处理安装的操作
   */
  private synchronized void handleInstall(Context context, String packageName) {
    ApkUtil.handleInstallAction(packageName);
    sendWbApkAction(context, ACTION_APK_INSTALL, packageName);
  }

  /**
   * 处理卸载的操作
   */
  private synchronized void handleUnInstall(Context context, String packageName) {
    ApkUtil.handleUnInstallAction(packageName);
    sendWbApkAction(context, ACTION_APK_UNINSTALL, packageName);
  }

  /**
   * 处理更新操作
   */
  private synchronized void handleUpdate(Context context, String packageName) {
    ApkUtil.handleUpdateAction(packageName);
    sendWbApkAction(context, ACTION_APK_UPDATE, packageName);
  }

  /**
   * 发送玩吧Apk
   */
  private void sendWbApkAction(Context context, String action, String packageName) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(context.getPackageName());
    Uri uri = builder.build();
    Intent intent = new Intent(action);
    intent.setData(uri);
    intent.putExtra(DATA_PKG_NAME, packageName);
    context.sendBroadcast(intent);
  }
}
