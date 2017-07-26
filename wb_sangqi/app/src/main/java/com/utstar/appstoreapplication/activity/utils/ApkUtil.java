package com.utstar.appstoreapplication.activity.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.Probe.ProbeService;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.db_entity.GameRecord;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.receiver.InstallReceiver;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.windows.game_detail.GameInstallDialog;
import com.utstar.upgrade.IUpgaradeInterface;
import com.wanba.util.IRestartWanba;
import java.io.File;
import java.util.List;
import org.litepal.crud.DataSupport;

/**
 * Created by Aria.Lao on 2017/3/2.
 */

public class ApkUtil {
  private static final String TAG = "ApkUtil";
  private static ServiceConnection mInstallConnection;
  private static ServiceConnection mUnInstallConnection;

  /**
   * 安装Apk回调
   */
  public interface InstallApkCallback {
    void callback(int result);
  }

  /**
   * 通过游戏Id加载游戏
   */
  public static void loadGame(Context context, int gameId, FragmentManager fm) {
    GameInstallDialog dialog = new GameInstallDialog(gameId);
    dialog.show(fm, "game_load_form_id");
  }

  /**
   * 通过游戏详情实体加载游戏
   */
  public static void loadGame(GameDetailEntity entity, FragmentManager fm) {
    GameInstallDialog dialog = new GameInstallDialog(entity);
    dialog.show(fm, "game_load_form_entity");
  }

  /**
   * 启动游戏，启动统计时长服务，并进行记录
   */
  public static void startGame(Context context, String packageName) {
    AndroidUtils.startOtherApp(context, packageName);
    ProbeService.start(context, packageName);
    GameRecord record = GameRecord.findData(GameRecord.class, "packageName=?", packageName);
    if (record == null) {
      DownloadInfo info = DownloadHelpUtil.getDownloadInfo(packageName);
      if (info == null) {
        return;
      }
      record = createGameRecord(packageName, info);
    }
    record.lastStartTime = System.currentTimeMillis();
    record.startNum = record.startNum + 1;
    record.save();
  }

  /**
   * 启动游戏，启动统计时长服务，并进行记录
   */
  public static void loadGame(Context context, GameDetailEntity entity, FragmentManager fm) {
    loadGame(entity, fm);
    ProbeService.start(context, entity.packageName);
    GameRecord record = GameRecord.findData(GameRecord.class, "packageName=?", entity.packageName);
    if (record == null) {
      DownloadInfo info = DownloadHelpUtil.getDownloadInfo(entity.packageName);
      if (info == null) {
        return;
      }
      record = createGameRecord(entity.packageName, info);
    }
    record.lastStartTime = System.currentTimeMillis();
    record.startNum = record.startNum + 1;
    record.save();
  }

  /**
   * 安装玩吧，安装过程交由第三方apk实现
   */
  public static void installWanba(Context context, final String path) {
    if (!AndroidUtils.isInstall(context, CommonConstant.UPGRADE_PACKAGE_NAME)) {
      AndroidUtils.install(context, new File(path));
      return;
    }
    final ServiceConnection connection = new ServiceConnection() {
      @Override public void onServiceConnected(ComponentName name, IBinder service) {
        IRestartWanba wanba = IRestartWanba.Stub.asInterface(service);
        try {
          wanba.installWanba(path);
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }

      @Override public void onServiceDisconnected(ComponentName name) {

      }
    };
    Intent intent = new Intent();
    intent.setAction(CommonConstant.RESTART_WANBA_UTIL_ACTION);
    intent.setPackage(CommonConstant.RESTART_WANBA_UTIL_PKG);
    context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  /**
   * 卸载APK，如果静默安装工具存在，则使用静默安装工具卸载APK，否则使用系统原生卸载方式
   *
   * @param packageName 需要卸载的游戏包名
   */
  public synchronized static void unInstallApk(Context context, String packageName) {
    unInstallApk(context, packageName, null);
  }

  /**
   * 卸载APK，如果静默安装工具存在，则使用静默安装工具卸载APK，否则使用系统原生卸载方式
   *
   * @param packageName 需要卸载的游戏包名
   */
  public synchronized static void unInstallApk(Context context, String packageName,
      InstallApkCallback callback) {
    if (AndroidUtils.isInstall(context, packageName)) {
      if (!AndroidUtils.isInstall(context, CommonConstant.UPGRADE_PACKAGE_NAME)) {
        AndroidUtils.uninstall(context, packageName);
      } else {
        mUnInstallConnection = new ServiceConnection() {
          private IUpgaradeInterface upgaradeInterface = null;

          @Override public void onServiceConnected(ComponentName name, IBinder service) {
            L.d(TAG, "upgrade Service Connected");
            upgaradeInterface = IUpgaradeInterface.Stub.asInterface(service);
            try {
              int result = upgaradeInterface.uninstall(packageName);
              if (callback != null) {
                callback.callback(result);
              }
            } catch (RemoteException e) {
              e.printStackTrace();
            } finally {
              context.unbindService(mUnInstallConnection);
            }
          }

          @Override public void onServiceDisconnected(ComponentName name) {
            L.d(TAG, "upgrade Service Disconnected");
            mUnInstallConnection = null;
          }
        };
        ComponentName componentName =
            new ComponentName("com.utstar.upgrade", "com.utstar.upgrade.UpgradeService");
        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(componentName);
        serviceIntent.setAction("com.utstar.upgrade.ACTION");
        boolean flag =
            context.bindService(serviceIntent, mUnInstallConnection, Context.BIND_AUTO_CREATE);
        L.e(TAG, "绑定静默安装工具的服务" + (flag ? "成功" : "失败!"));
      }
    }
  }

  /**
   * 安装APK，如果静默安装工具存在，则使用静默安装工具安装APK，否则使用系统原生安装方式
   *
   * @param apkPath apk路径
   */
  public synchronized static void installApk(Context context, String apkPath,
      InstallApkCallback callback) {
    File apk = new File(apkPath);
    if (!apk.exists()) {
      FL.e(TAG, "路径【" + apkPath + "】不存在！！");
      return;
    }
    if (!AndroidUtils.isInstall(context, CommonConstant.UPGRADE_PACKAGE_NAME)) {
      AndroidUtils.install(context, new File(apkPath));
    } else {
      mInstallConnection = new ServiceConnection() {
        private IUpgaradeInterface upgaradeInterface = null;

        @Override public void onServiceConnected(ComponentName name, IBinder service) {
          L.d(TAG, "upgrade Service Connected");
          upgaradeInterface = IUpgaradeInterface.Stub.asInterface(service);
          try {
            int result = upgaradeInterface.install(apkPath);
            if (callback != null) {
              callback.callback(result);
            }
            if (result != 0) {
              FL.e(TAG, "install apk fail，result：" + result + "，apkPath：" + apkPath);
              sendFailInfo(context, AndroidUtils.getApkPackageName(context, apkPath));
            }
          } catch (RemoteException e) {
            e.printStackTrace();
          } finally {
            try {
              context.unbindService(mInstallConnection);
            } catch (IllegalArgumentException e) {
              FL.e(TAG, FL.getExceptionString(e));
            }
          }
        }

        @Override public void onServiceDisconnected(ComponentName name) {
          L.d(TAG, "upgrade Service Disconnected");
          mInstallConnection = null;
        }
      };
      ComponentName componentName =
          new ComponentName("com.utstar.upgrade", "com.utstar.upgrade.UpgradeService");
      Intent serviceIntent = new Intent();
      serviceIntent.setComponent(componentName);
      serviceIntent.setAction("com.utstar.upgrade.ACTION");
      boolean flag =
          context.bindService(serviceIntent, mInstallConnection, Context.BIND_AUTO_CREATE);
      L.e(TAG, "绑定静默安装工具的服务" + (flag ? "成功" : "失败!"));
    }
  }

  /**
   * 发送安装失败的广播
   */
  private static void sendFailInfo(Context context, String packageName) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(context.getPackageName());
    Uri uri = builder.build();
    Intent intent = new Intent(InstallReceiver.ACTION_APK_INSTALL_FAIL);
    intent.setData(uri);
    intent.putExtra(InstallReceiver.DATA_PKG_NAME, packageName);
    context.sendBroadcast(intent);
  }

  /**
   * 安装APK，如果静默安装工具存在，则使用静默安装工具安装APK，否则使用系统原生安装方式
   *
   * @param apkPath apk路径
   */
  public static void installApk(Context context, String apkPath) {
    installApk(context, apkPath, null);
  }

  /**
   * 处理更新事件
   */
  public static void handleUpdateAction(String packageName) {
    List<DownloadInfo> infos =
        DownloadInfo.where("packageName = ?", packageName).find(DownloadInfo.class);
    if (GameUpdateUtil.isUpdate(packageName)) {
      GameUpdateUtil.delGameUpdateInfo(packageName);
    }
    if (infos != null && infos.size() > 0) {
      DataSupport.deleteAll(DownloadInfo.class, "packageName = ?", packageName);
      ActionUtil.updateAction(infos.get(0), CommonConstant.ACTION_UPDATE);
    } else {
      L.w("找不到该下载信息，packageName = " + packageName);
    }
  }

  /**
   * 处理Apk卸载事件
   */
  public static void handleUnInstallAction(String packageName) {
    DownloadInfo info = DownloadHelpUtil.getDownloadInfo(packageName);
    if (info != null) {
      DataSupport.deleteAll(DownloadInfo.class, "packageName = ?", packageName);
      ActionUtil.updateAction(info, CommonConstant.ACTION_UNINSTALL);
      DownloadEntity entity =
          DownloadEntity.findData(DownloadEntity.class, "downloadUrl=?", info.getDownloadUrl());
      if (entity != null) {
        entity.deleteData();
      }
    } else {
      L.w("找不到该下载信息，packageName = " + packageName);
    }
  }

  /**
   * 处理APk安装事件
   */
  public static void handleInstallAction(String packageName) {
    DownloadInfo info = DownloadHelpUtil.getDownloadInfo(packageName);
    if (info != null) {
      ActionUtil.updateAction(info, CommonConstant.ACTION_INSTALL);
      if (!TextUtils.isEmpty(info.getDownloadPath())) {
        FileUtil.deleteFile(info.getDownloadPath());
      }
      if (BuildConfig.DEBUG) {
        T.showShort(BaseApp.context, info.getGameName() + "安装完成");
      }
      createGameRecord(packageName, info);
      DataSupport.deleteAll(DownloadInfo.class, "packageName = ?", packageName);
    } else {
      L.w("找不到该下载信息，packageName = " + packageName);
    }
  }

  /**
   * 创建游戏记录
   */
  private static GameRecord createGameRecord(String packageName, DownloadInfo info) {
    GameRecord record = GameRecord.findData(GameRecord.class, "packageName=?", packageName);
    if (record == null) {
      record = new GameRecord();
    }
    record.gameIcon = info.getGameIcon();
    record.gameName = info.getGameName();
    record.packageName = info.getPackageName();
    record.installTime = System.currentTimeMillis();
    PackageInfo apkInfo = AndroidUtils.getAppInfo(BaseApp.application, packageName);
    if (apkInfo != null) {
      record.versionCode = apkInfo.versionCode;
      record.versionName = apkInfo.versionName;
    }
    record.save();
    return record;
  }
}
